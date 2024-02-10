import org.jelly.parse.token.*;
import org.jelly.utils.*;

TokenIterator tokensFromStrings(String... strs) {
    return new TokenIterator(new StringArrIterator(strs));
}

Token tiMaybeNext(TokenIterator ti) {
    if(ti.hasNext()){
        return ti.next();
    }
    else{
	System.out.println("DATTI FUOCO");
        return new NormalToken("Vaffanculo, sticazzi, muori, ammazzati");
    }
}
