(TeX-add-style-hook
 "evaluator"
 (lambda ()
   (TeX-add-to-alist 'LaTeX-provided-class-options
                     '(("article" "11pt")))
   (TeX-add-to-alist 'LaTeX-provided-package-options
                     '(("inputenc" "utf8") ("fontenc" "T1") ("ulem" "normalem") ("tcolorbox" "breakable" "xparse")))
   (add-to-list 'LaTeX-verbatim-environments-local "VerbEnv")
   (add-to-list 'LaTeX-verbatim-environments-local "SaveVerbatim")
   (add-to-list 'LaTeX-verbatim-environments-local "VerbatimOut")
   (add-to-list 'LaTeX-verbatim-environments-local "LVerbatim*")
   (add-to-list 'LaTeX-verbatim-environments-local "LVerbatim")
   (add-to-list 'LaTeX-verbatim-environments-local "BVerbatim*")
   (add-to-list 'LaTeX-verbatim-environments-local "BVerbatim")
   (add-to-list 'LaTeX-verbatim-environments-local "Verbatim*")
   (add-to-list 'LaTeX-verbatim-environments-local "Verbatim")
   (add-to-list 'LaTeX-verbatim-environments-local "lstlisting")
   (add-to-list 'LaTeX-verbatim-environments-local "minted")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "lstinline")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "href")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "hyperimage")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "hyperbaseurl")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "nolinkurl")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "url")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "path")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "Verb")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "Verb*")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "EscVerb")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "EscVerb*")
   (add-to-list 'LaTeX-verbatim-macros-with-delims-local "Verb*")
   (add-to-list 'LaTeX-verbatim-macros-with-delims-local "Verb")
   (add-to-list 'LaTeX-verbatim-macros-with-delims-local "lstinline")
   (add-to-list 'LaTeX-verbatim-macros-with-delims-local "path")
   (TeX-run-style-hooks
    "latex2e"
    "article"
    "art11"
    "inputenc"
    "fontenc"
    "graphicx"
    "longtable"
    "wrapfig"
    "rotating"
    "ulem"
    "amsmath"
    "amssymb"
    "capt-of"
    "hyperref"
    "color"
    "listings"
    "fvextra"
    "xcolor"
    "tcolorbox"
    "float")
   (TeX-add-symbols
    '("EFrdi" 1)
    '("EFrdh" 1)
    '("EFrdg" 1)
    '("EFrdf" 1)
    '("EFrde" 1)
    '("EFrdd" 1)
    '("EFrdc" 1)
    '("EFrdb" 1)
    '("EFrda" 1)
    '("EFhs" 1)
    '("EFhq" 1)
    '("EFhn" 1)
    '("EFob" 1)
    '("EFrb" 1)
    '("EFrc" 1)
    '("EFpp" 1)
    '("EFnc" 1)
    '("EFwr" 1)
    '("EFo" 1)
    '("EFt" 1)
    '("EFv" 1)
    '("EFf" 1)
    '("EFb" 1)
    '("EFk" 1)
    '("EFm" 1)
    '("EFd" 1)
    '("EFs" 1)
    '("EFcd" 1)
    '("EFc" 1)
    '("EFe" 1)
    '("EFw" 1)
    '("EFsc" 1)
    '("EFh" 1)
    '("EFD" 1)
    "listingsname"
    "listoflistingsname"
    "listoflistings"
    "efstrut")
   (LaTeX-add-labels
    "sec:org0cf434e"
    "sec:org1130a2e"
    "sec:orgd27f686"
    "sec:org8aaee45"
    "sec:orgf5557d2"
    "sec:org073ec05"
    "sec:org86c57f1")
   (LaTeX-add-xcolor-definecolors
    "EfD"
    "EFD"
    "EFh"
    "EFsc"
    "EFw"
    "EFe"
    "EFc"
    "EFcd"
    "EFs"
    "EFd"
    "EFm"
    "EFk"
    "EFb"
    "EFf"
    "EFv"
    "EFt"
    "EFo"
    "EFwr"
    "EFpp"
    "EFhn"
    "EFhq"
    "EFhs"
    "EFrda"
    "EFrdb"
    "EFrdc"
    "EFrdd"
    "EFrde"
    "EFrdf"
    "EFrdg"
    "EFrdh"
    "EFrdi"))
 :latex)

