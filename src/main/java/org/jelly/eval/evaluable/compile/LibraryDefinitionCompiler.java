package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.LibraryDefinitionEvaluable;
import org.jelly.eval.evaluable.SequenceEvaluable;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.library.ExportDirective;
import org.jelly.eval.library.ImportSet;
import org.jelly.eval.library.Registry;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Symbol;
import org.jelly.utils.AstHandling;
import org.jelly.utils.ConsUtils;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LibraryDefinitionCompiler implements FormCompiler {
    private final Cons form;
    public LibraryDefinitionCompiler(Cons form) {
        this.form = form;
    }

    @Override
    public LibraryDefinitionEvaluable compile() {
        return fromCheckedAST(form);
    }

    @Override
    public void check() throws MalformedFormException {
        checkAST(form);
    }

    @NotNull
    private static LibraryDefinitionEvaluable fromCheckedAST(Cons c) {
        Cons name = ConsUtils.requireCons(c.nth(1));
        List<Cons> libraryDeclarations = ConsUtils.toStream(ConsUtils.requireCons(c.nthCdr(2)))
                .map(ConsUtils::requireCons)
                .toList();
        List<ImportSet> imports = libraryDeclarations
                .stream()
                .filter(a -> AstHandling.requireSymbol(a.getCar()).name().equals("import"))
                .map(LibraryDefinitionCompiler::parseImportDeclaration)
                .toList();
        List<ExportDirective> exports = libraryDeclarations
                .stream()
                .filter(a -> AstHandling.requireSymbol(a.getCar()).name().equals("export"))
                .map(LibraryDefinitionCompiler::parseExportDeclaration)
                .toList();
        List<SequenceEvaluable> begins = libraryDeclarations
                .stream()
                .filter(a -> AstHandling.requireSymbol(a.getCar()).name().equals("begin"))
                .map(LibraryDefinitionCompiler::parseBeginDeclaration)
                .toList();

        return new LibraryDefinitionEvaluable(name, imports, begins, exports);
    }

    private static ImportSet parseImportDeclaration(Cons c) {
        List<ImportSet> imports = ConsUtils.toStream(ConsUtils.requireCons(c.getCdr()))
                .map(ConsUtils::requireCons)
                .map(LibraryDefinitionCompiler::parseImportDirective)
                .toList();

        ImportSet res = ImportSet.empty();
        for(ImportSet imp : imports) {
            res = ImportSet.join(res, imp);
        }
        return res;
    }

    private static ImportSet parseImportDirective(Cons c) {
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

    private static ExportDirective parseExportDeclaration(Cons c) {
        Map<Symbol, Symbol> exports = new HashMap<>();
        for(Object o : ConsUtils.toList(ConsUtils.requireCons(c.getCdr()))) {
            switch(o) {
                case Cons cn -> exports.put((Symbol)cn.nth(1), (Symbol)cn.nth(2));
                case Symbol s -> exports.put(s, s);
                case null -> throw new NullPointerException("bro");
                default -> throw new InvalidParameterException("bro");
            }
        }
        return new ExportDirective(exports);
    }

    private static SequenceEvaluable parseBeginDeclaration(Cons c) {
        return Utils.sequenceFromConsList(ConsUtils.requireCons(c.getCdr()));
    }

    private static void checkAST(Cons c) throws MalformedFormException {
        // library name
        Utils.checkSymbolList(ConsUtils.requireCons(c.nth(1)));

        // everything else
        for(Object elt : ConsUtils.toList(ConsUtils.requireCons(c.nthCdr(2)))) {
            Cons celt = ConsUtils.requireCons(elt);
            switch(AstHandling.requireSymbol(celt.getCar()).name()) {
                case "import" -> checkImportForm(celt);
                case "export" -> checkExportForm(celt);
                case "begin" -> checkBeginForm(celt);
            }
        }
    }

    private static void checkImportForm(Cons importForm) throws MalformedFormException {
        try {
            for(Object directive : ConsUtils.toList(ConsUtils.requireCons(importForm.getCdr()))) {
                switch(directive) {
                    case Cons c -> checkImportDirective(c);
                    case Symbol s -> Utils.nothing();
                    default -> throw new MalformedFormException("cannot follow import directive "
                            + directive
                            + " as it is of type "
                            + directive.getClass().getCanonicalName()
                            + " which is neither a symbol nor a list");
                }
            }
        } catch (ClassCastException cce) {
            throw new MalformedFormException("cannot compile import form because ast element is of incorrect type", cce);
        }
    }

    private static void checkImportDirective(Cons directive) throws MalformedFormException {
        try {
            Symbol sym = (Symbol) directive.getCar();
            switch (sym.name()) {
                case "only", "except" -> {
                    checkImportForm(ConsUtils.requireCons(ConsUtils.nth(directive, 1)));
                    Utils.checkSymbolList(ConsUtils.requireCons(ConsUtils.nthCdr(directive, 2)));
                }
                case "prefix" -> {
                    Utils.checkListOfSize(directive, 2);
                    checkImportForm(ConsUtils.requireCons(ConsUtils.nth(directive, 1)));
                    AstHandling.requireSymbol(ConsUtils.requireCons(ConsUtils.nth(directive, 2)));
                }
                case "rename" -> {
                    checkImportForm(ConsUtils.requireCons(ConsUtils.nth(directive, 1)));
                    Utils.checkSymbolAlist(ConsUtils.requireCons(ConsUtils.nthCdr(directive, 2)));
                }
            }
        } catch (ClassCastException cce) {
            throw new MalformedFormException("cannot compile import form because ast element is of incorrect type", cce);
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("import directive is malformed because child is malformed", mfe);
        }
    }

    private static void checkExportForm(Cons exportForm) throws MalformedFormException {
        for(Object elt : ConsUtils.toList(ConsUtils.requireCons(exportForm.getCdr()))) {
            if(elt instanceof Cons c) {
                Utils.checkListOfSize(c, 3);
                Utils.checkSymbolList(c);
                if(!AstHandling.requireSymbol(c.getCar()).name().equals("require")) {
                    throw new MalformedFormException("export directive can only be symbol or (rename <symbol> <symbol>), directive cannot start with " + c.getCar());
                }
            }
            else if(!(elt instanceof Symbol)) {
                throw new MalformedFormException("export form can only contain symbols or (rename <symbol> <symbol>) , it cannot contain "
                        + elt
                        + " of type "
                        + elt.getClass().getCanonicalName());
            }
        }
    }

    private static void checkBeginForm(Cons beginForm) throws MalformedFormException {
        Utils.checkSequenceList(ConsUtils.requireCons(beginForm.getCdr()));
    }
}
