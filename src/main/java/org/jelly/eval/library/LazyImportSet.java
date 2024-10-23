package org.jelly.eval.library;

import org.jelly.lang.data.ConsList;
import org.jelly.lang.data.Symbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class LazyImportSet {
    private final List<ConsList> libraryNames;
    private final List<LazyImportSet> parents;
    private ImportSet built = null;

    abstract ImportSet build(LibraryRegistry r);

    public List<ConsList> getLibraryNames() {
        return this.libraryNames;
    }

    public ConsList getFirstLibraryName() {
        return this.libraryNames.getFirst();
    }

    public ImportSet get(LibraryRegistry r) {
        if(built == null)
            built = build(r);
        return built;
    }

    private LazyImportSet(ConsList libraryName) {
        this.libraryNames = new ArrayList<>();
        libraryNames.addLast(libraryName);
        this.parents = null;
    }

    private LazyImportSet(LazyImportSet parent) {
        this.libraryNames = parent.libraryNames;
        this.parents = List.of(parent);
    }

    private LazyImportSet(LazyImportSet genitore1, LazyImportSet genitore2) {
        this.libraryNames = new ArrayList<>();
        this.libraryNames.addAll(genitore1.libraryNames);
        this.libraryNames.addAll(genitore2.libraryNames);
        this.parents = List.of(genitore1, genitore2);
    }

    public static LazyImportSet library (ConsList libraryName) {
        return new LazyImportSet(libraryName) {
            @Override
            ImportSet build(LibraryRegistry r) {
                return ImportSet.library(r.getLibrary(libraryName));
            }
        };
    }

    public static LazyImportSet only (LazyImportSet parent, Collection<Symbol> only) {
        return new LazyImportSet(parent) {
            @Override
            ImportSet build(LibraryRegistry r) {
                return ImportSet.only(parent.get(r), only);
            }
        };
    }

    public static LazyImportSet except(LazyImportSet parent, Collection<Symbol> except) {
        return new LazyImportSet(parent) {
            @Override
            ImportSet build(LibraryRegistry r) {
                return ImportSet.except(parent.get(r), except);
            }
        };
    }

   public static LazyImportSet prefix(LazyImportSet parent, Symbol prefix) {
       return new LazyImportSet(parent) {
           @Override
           ImportSet build(LibraryRegistry r) {
               return ImportSet.prefix(parent.get(r), prefix);
           }
       };
   }

   public static LazyImportSet rename(LazyImportSet parent, Map<Symbol, Symbol> renames) {
       return new LazyImportSet(parent) {
           @Override
           ImportSet build(LibraryRegistry r) {
               return ImportSet.rename(parent.get(r), renames);
           }
       };
   }

   public static LazyImportSet join(LazyImportSet a, LazyImportSet b) {
       return new LazyImportSet(a, b) {
           @Override
           ImportSet build(LibraryRegistry r) {
               return ImportSet.join(a.get(r), b.get(r));
           }
       };
   }
}
