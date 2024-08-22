package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.ImportEvaluable;
import org.jelly.eval.evaluable.compile.errors.MalformedFormException;
import org.jelly.eval.library.ImportSet;
import org.jelly.eval.library.Registry;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Symbol;
import org.jelly.utils.AstHandling;
import org.jelly.utils.ConsUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ImportCompiler implements FormCompiler {
    private final Cons form;

    public ImportCompiler(Cons c) {
        this.form = c;
    }

    @Override
    public void check() throws MalformedFormException{
        checkImportForm(form);
    }

    @Override
    public ImportEvaluable compile() {
        return new ImportEvaluable(parseImportDeclaration(form));
    }

    public static ImportSet parseImportDeclaration(Cons c) {
        List<ImportSet> imports = ConsUtils.toStream(ConsUtils.requireCons(c.getCdr()))
                .map(ConsUtils::requireCons)
                .map(ImportCompiler::parseImportDirective)
                .toList();

        ImportSet res = imports.getFirst();
        for(ImportSet imp : imports.subList(1, imports.size())) {
            res = ImportSet.join(res, imp);
        }
        return res;
    }

    public static ImportSet parseImportDirective(Cons c) {
        return switch(AstHandling.requireSymbol(c.getCar()).name()) {
            case "only" -> ImportSet.only(parseImportDirective(ConsUtils.requireCons(c.nth(1))),
                                              ConsUtils.toStream(ConsUtils.requireCons(c.nthCdr(2)))
                                                      .map(AstHandling::requireSymbol)
                                                      .collect(Collectors.toSet()));
            case "except" -> ImportSet.except(parseImportDirective(ConsUtils.requireCons(c.nth(1))),
                                              ConsUtils.toStream(ConsUtils.requireCons(c.nthCdr(2)))
                                                      .map(AstHandling::requireSymbol)
                                                      .collect(Collectors.toSet()));
            case "rename" -> ImportSet.rename(parseImportDirective(ConsUtils.requireCons(c.nth(1))),
                                              Utils.alistToMap(ConsUtils.requireCons(c.nthCdr(2))));
            case "prefix" -> ImportSet.prefix(parseImportDirective(ConsUtils.requireCons(c.nth(1))),
                                              AstHandling.requireSymbol(c.nth(2)));
            default -> ImportSet.library(Registry.getLibrary(c));
        };
    }

    public static void checkImportForm(Cons importForm) throws MalformedFormException {
        try {
            for(Object directive : ConsUtils.toList(ConsUtils.requireCons(importForm.getCdr()))) {
                switch(directive) {
                    case Cons c -> checkImportDirective(c);
                    default -> throw new MalformedFormException("cannot follow import directive "
                            + directive
                            + " as it is of type "
                            + directive.getClass().getCanonicalName()
                            + "which is not a symbol");
                }
            }
        } catch (ClassCastException cce) {
            throw new MalformedFormException("cannot compile import form because ast element is of incorrect type", cce);
        }
    }

    public static void checkImportDirective(Cons directive) throws MalformedFormException {
        try {
            Symbol sym = (Symbol) directive.getCar();
            switch (sym.name()) {
                case "only", "except" -> {
                    checkImportDirective(ConsUtils.requireCons(ConsUtils.nth(directive, 1)));
                    Utils.checkSymbolList(ConsUtils.requireCons(ConsUtils.nthCdr(directive, 2)));
                }
                case "prefix" -> {
                    Utils.checkListOfSize(directive, 3);
                    checkImportDirective(ConsUtils.requireCons(ConsUtils.nth(directive, 1)));
                    AstHandling.requireSymbol(ConsUtils.nth(directive, 2));
                }
                case "rename" -> {
                    checkImportDirective(ConsUtils.requireCons(ConsUtils.nth(directive, 1)));
                    Utils.checkSymbolAlist(ConsUtils.requireCons(ConsUtils.nthCdr(directive, 2)));
                }
            }
        } catch (ClassCastException cce) {
            throw new MalformedFormException("cannot compile import form because ast element is of incorrect type", cce);
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("import directive is malformed because child is malformed", mfe);
        }
    }
}
