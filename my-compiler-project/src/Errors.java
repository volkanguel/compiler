// Errors
//
// This class is used to generate warning and fatal error messages.

class Errors {
    static void fatal(int lineNum, int charNum, String msg) {
        System.err.println(lineNum + ":" + charNum + " **ERROR** " + msg);
    }

    static void warn(int lineNum, int charNum, String msg) {
        System.err.println(lineNum + ":" + charNum + " **WARNING** " + msg);
    }
    
    static void reportError(String errorType, int lineNum, int column) {
    	
    	switch(errorType) {
    		case "unterminated_String":
    			System.err.println("ERROR: unterminated String at line "+ lineNum +", column"+ column +"");
    			
    		default:
                System.err.println("Unknown error at line " + lineNum + ", column " + column);
                break;
    	}
    }
}
