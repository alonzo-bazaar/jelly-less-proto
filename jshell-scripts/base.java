import utils.*;
import parse.*;
import lang.*;
import eval.*;

PrefixIterator piFromString(String str) {
    return new PrefixIterator
	    (new StringCharIterator(str));    
}

SignificantCharsIterator sciFromString(String str) {
    return new SignificantCharsIterator
	    (new StringCharIterator(str));
}

TokenIterator tiFromString(String str) {
    return new TokenIterator
	    (new SignificantCharsIterator
	     (new StringCharIterator(str)));
}

ExpressionIterator fromString(String s) {
    return new ExpressionIterator
	    (new TokenIterator
	     (new SignificantCharsIterator
	      (new StringCharIterator(s))));
}

void tiMaybeNext(TokenIterator ti) {
    String altrimenti = "vaffanculo, niente";
    if(ti.hasNext()){
        ti.next();
    }
    else{
        System.out.println(altrimenti);
    }
}

PrefixIterator pi = piFromString("prefissi");
SignificantCharsIterator sci = sciFromString("      prova ");
TokenIterator ti = tiFromString("(    prova   )");
TokenIterator toot = tiFromString("ti voglio //bastonare\n bene");
ExpressionIterator ei = fromString("(let ((it \"be\")) it)");
toot = tiFromString("    // commento \n checco");
