package org.jelly.eval.library;

import org.jelly.lang.data.ConsList;
import org.jelly.lang.data.Symbol;

import java.util.Collection;
import java.util.Map;

public abstract class LazyImportSet {
    private final ConsList libraryName;
    private final LazyImportSet parent;
    private ImportSet built = null;

    abstract ImportSet build(LibraryRegistry r);

    public ConsList getLibraryName() {
        return this.libraryName;
    }

    public ImportSet get(LibraryRegistry r) {
        if(built == null)
            built = build(r);
        return built;
    }

    private LazyImportSet(ConsList libraryName) {
        this.libraryName = libraryName;
        this.parent = null;
    }

    private LazyImportSet(LazyImportSet parent) {
        this.libraryName = parent.libraryName;
        this.parent = parent;
    }

    public static LazyImportSet library (ConsList libraryName) {
        return new LazyImportSet(libraryName) {
            @Override
            ImportSet build(LibraryRegistry r) {
                return ImportSet.library(r.getLibrary(libraryName), libraryName);
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
       return new LazyImportSet(a) {
           @Override
           ImportSet build(LibraryRegistry r) {
               return ImportSet.join(a.get(r), b.get(r));
           }
       };
   }
}
