package no.uit.smartcard;

/**
 * An exception in those cases a card not supported on the reader is inserted
 * @author Alexander Svendsen
 *
 */
public class InvalidCardException extends Exception{
	 String mistake;
	  public InvalidCardException()
	  {
	    super();             // call superclass constructor
	    mistake = "unknown";
	  }
	  
	  public InvalidCardException(String err)
	  {
	    super(err);     // call super class constructor
	    mistake = err;  // save message
	  }
	  
	  public String getError(){
		  return mistake;
	  }
}
