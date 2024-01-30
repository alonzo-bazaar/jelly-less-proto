package org.jelly.eval.procedure;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.jelly.eval.runtime.EnvFrame;
import org.jelly.eval.utils.ArgUtils;
import org.jelly.eval.utils.Utils;
import org.jelly.lang.data.LispList;
import org.jelly.lang.data.LispSymbol;
import org.jelly.lang.errors.SynthaxTreeParsingException;

public class ParameterList {
    private List<LispSymbol> positional;
    private LispSymbol rest;
    public static ParameterList fromLispList (LispList ll) {
        ParameterList res = new ParameterList();
        res.positional = Utils.toStream(ll)
                .takeWhile(a -> !((LispSymbol)a).getName().equals("&rest"))
                .map(a -> (LispSymbol)a)
                .toList();
        List<LispSymbol> restSyms = Utils.toStream(ll)
                .dropWhile(a -> !((LispSymbol)a).getName().equals("&rest"))
                .map(a -> (LispSymbol)a)
                .toList();

        switch(restSyms.size()) {
            case 0:
                res.rest = null;
                break;
            case 1:
                throw new SynthaxTreeParsingException("cannot have &rest followed by no actual &rest arguments");
            case 2:
                res.rest = restSyms.get(1);
                break;
            default:
                throw new SynthaxTreeParsingException("cannot have more than one &rest argument in parameter list");
        }

        return res;
    }

    public EnvFrame bind(List<Object> vals) throws BadParameterBindException {
        if(this.rest == null) {
            if (vals.size() != positional.size()) {
                throw new BadParameterBindException(positional.size(), vals.size(), BadParameterBindException.Expectancy.EXACTLY);
            }
            return new EnvFrame(this.positional, vals);
        }
        else {
            if ((vals.size()) <= positional.size()) {
                throw new BadParameterBindException(positional.size(), vals.size(), BadParameterBindException.Expectancy.AT_LEAST);
            }
            Map<LispSymbol, Object> binds = new HashMap<>();
            for(int i = 0; i< positional.size(); ++i) {
                binds.put(positional.get(i), vals.get(i));
            }
            binds.put(this.rest, ArgUtils.javaListToCons(vals.stream().skip(this.positional.size()).toList()));

            return new EnvFrame(binds);
        }
    }
}
