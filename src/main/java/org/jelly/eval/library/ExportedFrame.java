package org.jelly.eval.library;

import org.jelly.eval.environment.EnvFrame;
import org.jelly.lang.data.Symbol;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExportedFrame extends EnvFrame {
    private final Library lib;
    // redirects map to allow internal library to be modified and update the values to the exterior
    // direct map to make it also work as a normal envframe
    // all map interface destructive operations are performed on the direct map
    private final Map<Symbol, Symbol> redirects;
    private final Map<Symbol, Object> direct;

    public ExportedFrame(Library lib) {
        this.lib = lib;
        this.redirects = new HashMap<>();
        this.direct = new HashMap<>();
    }

    public ExportedFrame(ExportedFrame orig) {
        this.lib = orig.lib;
        this.redirects = orig.redirects;
        this.direct = orig.direct;
    }

    public void directExport(Symbol exposed, Symbol internal) {
        redirects.put(exposed, internal);
    }

    @Override
    public Object get(Object sym) {
        if(sym instanceof Symbol && redirects.containsKey(sym)) {
            Symbol internal = redirects.get(sym);
            return lib.getBindngsFrame().get(internal);
        }
        return direct.get(sym);
    }

    @Override
    public Object put(Symbol sym, Object val) {
        return direct.put(sym, val);
    }

    @Override
    public boolean containsKey(Object sym) {
        return  direct.containsKey(sym) || redirects.containsKey(sym);
    }

    @Override
    public boolean containsValue(Object val) {
        return direct.containsValue(val)
                || redirects.values().stream().anyMatch(a -> lib.getBindngsFrame().get(a).equals(val));
    }

    @Override
    public @NotNull Set<Symbol> keySet() {
        return Stream.concat(redirects.keySet().stream(), direct.keySet().stream()).collect(Collectors.toSet());
    }

    @Override
    public int size() {
        return redirects.size() + direct.size();
    }

    @Override
    public boolean isEmpty() {
        return redirects.isEmpty() && direct.isEmpty();
    }

    @Override
    public @NotNull Set<Entry<Symbol, Object>> entrySet() {
        Set<Entry<Symbol, Object>> res = new HashSet<>();
        redirects.forEach((internal, external) ->
                res.add(new AbstractMap.SimpleEntry<>(external, lib.getBindngsFrame().get(internal))));
        direct.forEach((key, value) ->
                res.add(new AbstractMap.SimpleEntry<>(key, value)));
        return res;
    }

    @Override
    public Object remove(Object sym) {
        // return direct.remove(sym);
        throw new UnsupportedOperationException("cannot delete from env frame");
    }

    @Override
    public @NotNull Collection<Object> values() {
        return Stream.concat(direct.values().stream(),
                             redirects.values().stream().map(a -> lib.getBindngsFrame().get(a)))
                    .collect(Collectors.toSet());
    }

    @Override
    public void putAll(Map<? extends Symbol, ? extends Object> map) {
        direct.putAll(map);
    }

    @Override
    public void clear() {
        // direct.clear();
        throw new UnsupportedOperationException("cannot delete from env frame");
    }

    public ExportedFrame only(Collection<Symbol> only) {
        Optional<Symbol> error = mismatchedKey(only);
        if(error.isPresent()) {
            throw new InvalidParameterException("only import set specifies symbol "
                    + error.get() + " which is not present in the original import set");
        }

        ExportedFrame orig = this;
        return new ExportedFrame(orig) {
            @Override
            public boolean containsKey(Object o) {
                return orig.containsKey(o) && only.contains(o);
            }

            @Override
            public Object get(Object key) {
                if(containsKey(key)) {
                    return orig.get(key);
                }
                return null;
            }
        };
    }

    public ExportedFrame except(Collection<Symbol> except) {
        Optional<Symbol> error = mismatchedKey(except);
        if(error.isPresent()) {
            throw new InvalidParameterException("except import set specifies symbol "
                    + error.get() + " which is not present in the original import set");
        }

        ExportedFrame orig = this;
        return new ExportedFrame(orig) {
            @Override
            public boolean containsKey(Object key) {
                return orig.containsKey(key) && (!except.contains(key)) ;
            }

            @Override
            public Object get(Object key) {
                if(containsKey(key)) {
                    return this.get(key);
                }
                return null;
            }
        };
    }
    
    public ExportedFrame prefix(String prefix) {
        ExportedFrame orig = this;
        return new ExportedFrame(orig) {
            @Override
            public boolean containsKey(Object key) {
                if(key instanceof Symbol s && s.name().startsWith(prefix)) {
                    return orig.containsKey(new Symbol(s.name().substring(prefix.length())));
                }
                return false;
            }

            @Override
            public Object get(Object key) {
                if(key instanceof Symbol s && s.name().startsWith(prefix)) {
                    return orig.get(new Symbol(s.name().substring(prefix.length())));
                }
                return null;
            }
        };
    }

    public ExportedFrame rename(Map<Symbol, Symbol> renames) {
        Optional<Symbol> error = mismatchedKey(renames.keySet());
        if(error.isPresent()) {
            throw new InvalidParameterException("rename import set renames symbol "
                    + error.get() + " which is not present in the original import set");
        }

        ExportedFrame orig = this;

        Set<Symbol> contained = new HashSet<>(orig.keySet());
        contained.removeAll(redirects.keySet());
        contained.addAll(redirects.values());
        return new ExportedFrame(orig) {

            @Override
            public boolean containsKey(Object key) {
                return key instanceof Symbol && contained.contains(key);
            }

            @Override
            public Object get(Object key) {
                if(containsKey(key)) {
                    if(key instanceof Symbol && redirects.containsKey(key))
                        return orig.get(redirects.get(key));
                    return orig.get(key);
                }
                return null;
            }
        };
    }

    public ExportedFrame join(ExportedFrame other) {
        ExportedFrame orig = this;
        Optional<Symbol> intersect = anyIntersection(other);
        if(intersect.isPresent()) {
            throw new InvalidParameterException("both import sets contain the symbol "
                    + intersect + ", cannot join import sets");
        }

        return new ExportedFrame(orig) {

            @Override
            public boolean containsKey(Object key) {
                return orig.containsKey(key) || other.containsKey(key);
            }

            @Override
            public Object get(Object key) {
                Object res = orig.get(key);
                if(res == null)
                    return other.get(key);
                return res;
            }
        };
    };

    private Optional<Symbol> mismatchedKey(Collection<Symbol> missingKey) {
        if(this.keySet().containsAll(missingKey))
            return Optional.empty();
        return missingKey.stream().dropWhile(this.keySet()::contains).findFirst();
    }

    private @NotNull Optional<Symbol> anyIntersection(@NotNull ExportedFrame other) {
        return this.keySet().stream().filter(other::containsKey).findFirst();
    }
}
