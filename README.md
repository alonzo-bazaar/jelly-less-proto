# Statement
Il seguente elaborato mira alla progettazione e produzione di un interpreter embeddable per un linguaggio lisp invocabile come script interno da un applicativo java, o utilizzabile come linguaggio a se stante.

Con embeddable si intende che l'interpreter è progettato in modo da facilitare la collaborazione e comunicazione tra se stesso e codice java, favorendo un facile inserimento di questo all'interno di diversi applicativi.

L'applicativo è inteso per essere utilizzabile come layer interattivo all'interno dell'applicazione(tramite shell) e/o come componete di scripting e configurazione per questa, come fatto ad esempio da autolisp o emacs lisp

L'interpreter può essere utilizzato come applicazione standalone, ad esempio da cli, per esecuzione in batch di script e/o per esecuzione interattiva di codice tramite una shell simile a python.

Sarà inoltre possibile eseguire codice, da file o stringhe, all'interno del codice java, permettendo una comunicazione ancora più diretta tra i due strati.


