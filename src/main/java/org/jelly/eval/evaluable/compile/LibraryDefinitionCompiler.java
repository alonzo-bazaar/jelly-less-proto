package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.LibraryDefinitionEvaluable;
import org.jelly.eval.evaluable.SequenceEvaluable;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.library.ExportDirective;
import org.jelly.eval.library.ImportSet;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Symbol;
import org.jelly.utils.AstHandling;
import org.jelly.utils.ConsUtils;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .map(ImportCompiler::parseImportDeclaration)
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
                case "import" -> ImportCompiler.checkImportForm(celt);
                case "export" -> checkExportForm(celt);
                case "begin" -> checkBeginForm(celt);
            }
        }
    }

    private static void checkExportForm(Cons exportForm) throws MalformedFormException {
        for(Object elt : ConsUtils.toList(ConsUtils.requireCons(exportForm.getCdr()))) {
            if(elt instanceof Cons c) {
                Utils.checkListOfSize(c, 3);
                Utils.checkSymbolList(c);
                if(!AstHandling.requireSymbol(c.getCar()).name().equals("rename")) {
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
