package org.jelly.eval.evaluable.procedure;

import java.util.List;

import org.jelly.eval.evaluable.procedure.errors.BadParameterBindException;
import org.jelly.eval.environment.EnvFrame;
import org.jelly.eval.builtinfuns.Utils;
import org.jelly.lang.data.Constants;
import org.jelly.lang.data.LispSymbol;
import org.jelly.parse.errors.SynthaxTreeParsingException;

public class LambdaList {
    private List<LispSymbol> positional;
    private LispSymbol restSym;

    public static LambdaList fromList(List<LispSymbol> ll) {
        LambdaList res = new LambdaList();
        res.positional = ll.stream().takeWhile(a -> !(a.getName().equals("&rest"))).toList();
        List<LispSymbol> restSyms = ll.stream().dropWhile(a -> !(a.getName().equals("&rest"))).toList();

        // restSyms should be empty or (&rest ...), if ... is just one symbol then we have rest, otherwise it's an error
        switch(restSyms.size()) {
            case 0:
                res.restSym = null;
                break;
            case 1:
                throw new SynthaxTreeParsingException("cannot have &rest followed by no actual &rest arguments");
            case 2:
                res.restSym = restSyms.get(1);
                break;
            default:
                throw new SynthaxTreeParsingException("cannot have more than one &rest argument in parameter list");
        }

        return res;
    }

    public EnvFrame bind(List<Object> vals) throws BadParameterBindException {
        if(this.restSym == null) {
            if (vals.size() != positional.size()) {
                throw new BadParameterBindException("expected to have exactly " + positional.size() + " parameters, but " + vals.size() + " parameters were given instead");
            }
            return new EnvFrame(this.positional, vals);
        }
        else {
            if (vals.size() < positional.size()) {
                throw new BadParameterBindException("expected to have at least " + positional.size() + " parameters, but " + vals.size() + " parameters were given instead");
            }
            else if(vals.size() == positional.size()) {
                EnvFrame frame = new EnvFrame(this.positional, vals);
                frame.bind(restSym, Constants.NIL); // empty list
                return frame;
            }
            else { // vals.size() > positional.size()
                EnvFrame frame = new EnvFrame(this.positional,vals.subList(0, positional.size()));
                frame.bind(restSym, Utils.javaListToCons(vals.subList(positional.size(), vals.size())));
                return frame;
            }
        }
    }
}
