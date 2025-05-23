/* The following code was generated by JFlex 1.7.0 */

import java_cup.runtime.*; // defines the Symbol class

// The generated scanner will return a Symbol for each token that it finds.
// A Symbol contains an Object field named value; that field will be of type
// TokenVal, defined below.
//
// A TokenVal object contains the line number on which the token occurs as
// well as the number of the character on that line that starts the token.
// Some tokens (e.g., literals) also include the value of the token.

class TokenVal {
 // fields
    int linenum;
    int charnum;
 // constructor
    TokenVal(int l, int c) {
        linenum = l;
	charnum = c;
    }
}

class IntLitTokenVal extends TokenVal {
 // new field: the value of the integer literal
    int intVal;
 // constructor
    IntLitTokenVal(int l, int c, int val) {
        super(l,c);
	intVal = val;
    }
}

class StringTokenVal extends TokenVal{
	String id;

	StringTokenVal(int l, int c, String id){
	super(l,c);
	this.id = id;
	}
}


class IdTokenVal extends TokenVal{
	String idVal;
	

	IdTokenVal(int l, int c, String idVal ){
	super(l,c);
	this.idVal = idVal;
	
	}
}


// The following class is used to keep track of the character number at which
// the current token starts on its line.
class CharNum {
  static int num=1;
}

class Keywordseen {
  static int INT = 0;
  static int STRING = 0;
}


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.7.0
 * from the specification file <tt>simple.jlex</tt>
 */
class Yylex implements java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0, 0
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\12\1\13\1\57\1\57\1\12\22\0\1\12\1\45\1\6"+
    "\1\0\1\4\1\0\1\50\1\10\1\55\1\56\1\40\1\41\1\52"+
    "\1\1\1\3\1\37\12\2\1\43\1\42\1\46\1\44\1\47\2\0"+
    "\22\4\1\34\7\4\1\0\1\7\2\0\1\5\1\0\1\24\1\33"+
    "\1\32\1\21\1\20\1\23\1\35\1\15\1\16\2\4\1\17\1\4"+
    "\1\11\1\22\1\31\1\4\1\27\1\25\1\26\1\30\1\36\1\14"+
    "\3\4\1\53\1\51\1\54\7\0\1\57\u1fa2\0\1\57\1\57\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\udfe6\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\1\2\1\3\1\4\1\5\1\3\1\6"+
    "\1\7\1\10\15\5\1\11\1\12\1\13\1\14\1\15"+
    "\1\16\1\17\1\20\1\21\2\1\1\22\1\23\1\24"+
    "\1\25\1\26\1\0\1\3\1\27\1\0\2\5\1\30"+
    "\2\5\1\31\14\5\1\32\1\33\1\34\1\35\1\36"+
    "\1\37\1\40\1\41\1\42\1\3\2\6\1\5\1\43"+
    "\16\5\1\0\1\44\2\0\1\5\1\45\4\5\1\46"+
    "\4\5\1\47\2\5\1\50\1\51\1\6\1\52\1\5"+
    "\1\53\3\5\1\54\1\5\1\55\2\5\1\0\1\5"+
    "\1\56\1\57\1\60\1\61\1\5\1\62\1\63\1\64";

  private static int [] zzUnpackAction() {
    int [] result = new int[131];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\60\0\140\0\220\0\300\0\360\0\360\0\u0120"+
    "\0\u0150\0\60\0\u0180\0\u01b0\0\u01e0\0\u0210\0\u0240\0\u0270"+
    "\0\u02a0\0\u02d0\0\u0300\0\u0330\0\u0360\0\u0390\0\u03c0\0\u03f0"+
    "\0\u0420\0\140\0\60\0\60\0\u0450\0\u0480\0\u04b0\0\u04e0"+
    "\0\u0510\0\u0540\0\60\0\60\0\60\0\60\0\60\0\u0570"+
    "\0\u05a0\0\60\0\u05d0\0\u0600\0\u0630\0\360\0\u0660\0\u0690"+
    "\0\360\0\u06c0\0\u06f0\0\u0720\0\u0750\0\u0780\0\u07b0\0\u07e0"+
    "\0\u0810\0\u0840\0\u0870\0\u08a0\0\u08d0\0\u0900\0\u0930\0\60"+
    "\0\60\0\60\0\60\0\60\0\60\0\60\0\u0960\0\u0990"+
    "\0\u09c0\0\u09f0\0\360\0\u0a20\0\u0a50\0\u0a80\0\u0ab0\0\u0ae0"+
    "\0\u0b10\0\u0b40\0\u0b70\0\u0ba0\0\u0bd0\0\u0c00\0\u0c30\0\u0c60"+
    "\0\u0c90\0\u0cc0\0\60\0\u0cf0\0\u0d20\0\u0d50\0\360\0\u0d80"+
    "\0\u0db0\0\u0de0\0\u0e10\0\360\0\u0e40\0\u0e70\0\u0ea0\0\u0ed0"+
    "\0\360\0\u0f00\0\u0f30\0\360\0\60\0\u0f60\0\360\0\u0f90"+
    "\0\360\0\u0fc0\0\u0ff0\0\u1020\0\360\0\u1050\0\360\0\u1080"+
    "\0\u10b0\0\u10e0\0\u1110\0\360\0\360\0\360\0\360\0\u1140"+
    "\0\360\0\360\0\360";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[131];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\1\3\1\4\1\5\1\6\1\7\1\10\2\2"+
    "\1\6\1\11\1\12\1\13\1\6\1\14\1\6\1\15"+
    "\1\16\1\6\1\17\1\6\1\20\1\21\1\22\1\6"+
    "\1\23\1\24\1\25\1\26\1\6\1\27\1\30\1\31"+
    "\1\32\1\33\1\34\1\35\1\36\1\37\1\40\1\41"+
    "\1\42\1\43\1\44\1\45\1\46\1\47\63\0\1\4"+
    "\1\5\55\0\1\50\1\4\1\5\2\51\3\0\1\51"+
    "\2\0\23\51\2\0\1\50\20\0\2\5\56\0\1\6"+
    "\1\0\2\6\3\0\1\6\2\0\23\6\21\0\6\10"+
    "\1\52\1\53\50\10\12\0\1\11\47\0\1\6\1\0"+
    "\2\6\3\0\1\6\2\0\1\6\1\54\21\6\23\0"+
    "\1\6\1\0\2\6\3\0\1\55\2\0\7\6\1\56"+
    "\13\6\23\0\1\6\1\0\2\6\3\0\1\6\2\0"+
    "\3\6\1\57\17\6\23\0\1\6\1\0\2\6\3\0"+
    "\1\6\2\0\4\6\1\60\1\6\1\61\14\6\23\0"+
    "\1\6\1\0\2\6\3\0\1\6\2\0\10\6\1\62"+
    "\12\6\23\0\1\6\1\0\2\6\3\0\1\6\2\0"+
    "\1\63\11\6\1\64\10\6\23\0\1\6\1\0\2\6"+
    "\3\0\1\6\2\0\13\6\1\65\7\6\23\0\1\6"+
    "\1\0\2\6\3\0\1\6\2\0\4\6\1\66\16\6"+
    "\23\0\1\6\1\0\2\6\3\0\1\6\2\0\13\6"+
    "\1\67\1\70\6\6\23\0\1\6\1\0\2\6\3\0"+
    "\1\6\2\0\3\6\1\71\4\6\1\72\12\6\23\0"+
    "\1\6\1\0\2\6\3\0\1\6\2\0\6\6\1\73"+
    "\14\6\23\0\1\6\1\0\2\6\3\0\1\6\2\0"+
    "\12\6\1\74\10\6\23\0\1\6\1\0\2\6\3\0"+
    "\1\6\2\0\6\6\1\75\14\6\60\0\1\76\1\77"+
    "\57\0\1\100\63\0\1\101\57\0\1\102\57\0\1\103"+
    "\57\0\1\104\63\0\1\105\60\0\1\106\10\0\1\107"+
    "\57\0\1\51\1\0\2\51\3\0\1\51\2\0\23\51"+
    "\21\0\6\110\3\10\1\111\14\110\2\111\30\110\2\0"+
    "\1\6\1\0\2\6\3\0\1\6\2\0\2\6\1\112"+
    "\20\6\23\0\1\6\1\0\2\6\3\0\1\6\2\0"+
    "\12\6\1\113\10\6\23\0\1\6\1\0\2\6\3\0"+
    "\1\6\2\0\11\6\1\114\11\6\23\0\1\6\1\0"+
    "\2\6\3\0\1\6\2\0\7\6\1\115\13\6\23\0"+
    "\1\6\1\0\2\6\3\0\1\6\2\0\3\6\1\116"+
    "\17\6\23\0\1\6\1\0\2\6\3\0\1\6\2\0"+
    "\2\6\1\117\20\6\23\0\1\6\1\0\2\6\3\0"+
    "\1\6\2\0\10\6\1\120\12\6\23\0\1\6\1\0"+
    "\2\6\3\0\1\6\2\0\14\6\1\121\6\6\23\0"+
    "\1\6\1\0\2\6\3\0\1\6\2\0\12\6\1\122"+
    "\10\6\23\0\1\6\1\0\2\6\3\0\1\6\2\0"+
    "\2\6\1\123\20\6\23\0\1\6\1\0\2\6\3\0"+
    "\1\6\2\0\17\6\1\124\3\6\23\0\1\6\1\0"+
    "\2\6\3\0\1\6\2\0\10\6\1\125\12\6\23\0"+
    "\1\6\1\0\2\6\3\0\1\6\2\0\11\6\1\126"+
    "\11\6\23\0\1\6\1\0\2\6\3\0\1\6\2\0"+
    "\6\6\1\127\14\6\23\0\1\6\1\0\2\6\3\0"+
    "\1\6\2\0\13\6\1\130\7\6\23\0\1\6\1\0"+
    "\2\6\3\0\1\6\2\0\2\6\1\131\20\6\21\0"+
    "\13\76\1\0\44\76\40\77\1\132\17\77\1\0\1\50"+
    "\1\107\1\0\2\51\3\0\1\51\2\0\23\51\2\0"+
    "\1\50\16\0\6\110\1\133\1\134\50\110\6\111\1\52"+
    "\1\135\50\111\2\0\1\6\1\0\2\6\3\0\1\6"+
    "\2\0\3\6\1\136\17\6\23\0\1\6\1\0\2\6"+
    "\3\0\1\6\2\0\4\6\1\137\16\6\23\0\1\6"+
    "\1\0\2\6\3\0\1\6\2\0\10\6\1\140\12\6"+
    "\23\0\1\6\1\0\2\6\3\0\1\6\2\0\11\6"+
    "\1\141\11\6\23\0\1\6\1\0\2\6\3\0\1\6"+
    "\2\0\12\6\1\142\10\6\23\0\1\6\1\0\2\6"+
    "\3\0\1\6\2\0\12\6\1\143\10\6\23\0\1\6"+
    "\1\0\2\6\3\0\1\6\2\0\4\6\1\144\16\6"+
    "\23\0\1\6\1\0\2\6\3\0\1\6\2\0\14\6"+
    "\1\145\6\6\23\0\1\6\1\0\2\6\3\0\1\146"+
    "\2\0\23\6\23\0\1\6\1\0\2\6\3\0\1\6"+
    "\2\0\3\6\1\147\17\6\23\0\1\6\1\0\2\6"+
    "\3\0\1\6\2\0\11\6\1\150\11\6\23\0\1\6"+
    "\1\0\2\6\3\0\1\6\2\0\4\6\1\151\16\6"+
    "\23\0\1\6\1\0\2\6\3\0\1\6\2\0\3\6"+
    "\1\152\17\6\23\0\1\6\1\0\2\6\3\0\1\6"+
    "\2\0\2\6\1\153\20\6\23\0\1\6\1\0\2\6"+
    "\3\0\1\6\2\0\5\6\1\154\15\6\21\0\37\77"+
    "\1\155\20\77\11\110\1\156\14\110\2\156\30\110\6\156"+
    "\4\111\14\156\2\111\30\156\2\0\1\6\1\0\2\6"+
    "\3\0\1\6\2\0\4\6\1\157\16\6\23\0\1\6"+
    "\1\0\2\6\3\0\1\6\2\0\14\6\1\160\6\6"+
    "\23\0\1\6\1\0\2\6\3\0\1\6\2\0\4\6"+
    "\1\161\16\6\23\0\1\6\1\0\2\6\3\0\1\6"+
    "\2\0\16\6\1\162\4\6\23\0\1\6\1\0\2\6"+
    "\3\0\1\6\2\0\2\6\1\163\20\6\23\0\1\6"+
    "\1\0\2\6\3\0\1\6\2\0\13\6\1\164\7\6"+
    "\23\0\1\6\1\0\2\6\3\0\1\6\2\0\12\6"+
    "\1\165\10\6\23\0\1\6\1\0\2\6\3\0\1\6"+
    "\2\0\2\6\1\166\20\6\23\0\1\6\1\0\2\6"+
    "\3\0\1\6\2\0\11\6\1\167\11\6\23\0\1\6"+
    "\1\0\2\6\3\0\1\6\2\0\4\6\1\170\16\6"+
    "\23\0\1\6\1\0\2\6\3\0\1\171\2\0\23\6"+
    "\21\0\6\156\1\0\1\172\50\156\2\0\1\6\1\0"+
    "\2\6\3\0\1\6\2\0\3\6\1\173\17\6\23\0"+
    "\1\6\1\0\2\6\3\0\1\6\2\0\1\6\1\174"+
    "\21\6\23\0\1\6\1\0\2\6\3\0\1\6\2\0"+
    "\16\6\1\175\4\6\23\0\1\6\1\0\2\6\3\0"+
    "\1\176\2\0\23\6\23\0\1\6\1\0\2\6\3\0"+
    "\1\6\2\0\16\6\1\177\4\6\23\0\1\6\1\0"+
    "\2\6\3\0\1\6\2\0\10\6\1\200\12\6\23\0"+
    "\1\6\1\0\2\6\3\0\1\6\2\0\21\6\1\201"+
    "\1\6\21\0\60\156\2\0\1\6\1\0\2\6\3\0"+
    "\1\6\2\0\12\6\1\202\10\6\23\0\1\6\1\0"+
    "\2\6\3\0\1\203\2\0\23\6\21\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[4464];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\11\7\1\1\11\20\1\2\11\6\1\5\11"+
    "\1\0\1\1\1\11\1\0\24\1\7\11\23\1\1\0"+
    "\1\11\2\0\17\1\1\11\14\1\1\0\11\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[131];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true iff the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true iff the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;
  
  /** 
   * The number of occupied positions in zzBuffer beyond zzEndRead.
   * When a lead/high surrogate has been read from the input stream
   * into the final zzBuffer position, this will have a value of 1;
   * otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  Yylex(java.io.Reader in) {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x110000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 172) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzBuffer.length*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;
    int numRead = zzReader.read(zzBuffer, zzEndRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      throw new java.io.IOException("Reader returned 0 characters. See JFlex examples for workaround.");
    }
    if (numRead > 0) {
      zzEndRead += numRead;
      /* If numRead == requested, we might have requested to few chars to
         encode a full Unicode character. We assume that a Reader would
         otherwise never return half characters. */
      if (numRead == requested) {
        if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    zzFinalHighSurrogate = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
    if (zzBuffer.length > ZZ_BUFFERSIZE)
      zzBuffer = new char[ZZ_BUFFERSIZE];
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      int zzCh;
      int zzCharCount;
      for (zzCurrentPosL = zzStartRead  ;
           zzCurrentPosL < zzMarkedPosL ;
           zzCurrentPosL += zzCharCount ) {
        zzCh = Character.codePointAt(zzBufferL, zzCurrentPosL, zzMarkedPosL);
        zzCharCount = Character.charCount(zzCh);
        switch (zzCh) {
        case '\u000B':  // fall through
        case '\u000C':  // fall through
        case '\u0085':  // fall through
        case '\u2028':  // fall through
        case '\u2029':
          yyline++;
          zzR = false;
          break;
        case '\r':
          yyline++;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
          }
          break;
        default:
          zzR = false;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
          { 
    return new Symbol(sym.EOF);
 }
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { Errors.fatal(yyline+1, CharNum.num,
			 "ignoring illegal character: " + yytext());

		Keywordseen.INT=0;
		Keywordseen.STRING=0;
	    	CharNum.num++;
            } 
            // fall through
          case 53: break;
          case 2: 
            { Symbol S = new Symbol(sym.MINUS, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 54: break;
          case 3: 
            { String identifier = yytext();

			if(Keywordseen.INT == 1||Keywordseen.STRING==1){
				System.err.println("ERROR: invalid Identifier at line " + yyline+1 +"  ("+yytext()+")");
			}
			else{
				if(!identifier.matches("[-]?[0-9]+")){
				System.err.println("ERROR: invalide Identifier at line " + yyline+1 +", Character " +CharNum.num+ " ("+yytext()+")");
				}
				else{
					try{

						int val = (new Integer(yytext())).intValue();
	    					Symbol S = new Symbol(sym.INTLITERAL,
			          		new IntLitTokenVal(yyline+1, CharNum.num, val)
						);
	    					CharNum.num += yytext().length();
	    					return S;
					} catch (NumberFormatException e){
						System.err.println("Fehler: Ungültige Zahl - " + e.getMessage());
        				}

				}
			}
			CharNum.num += yytext().length();
			Keywordseen.INT=0;
			Keywordseen.STRING=0;
            } 
            // fall through
          case 55: break;
          case 4: 
            { System.err.println("ERROR: invalid Integer at line " + yyline+1 +"  ("+yytext()+")");
            } 
            // fall through
          case 56: break;
          case 5: 
            { Keywordseen.INT=0;   // int hallo = 5;  ohne zurücksetzen wäre 5 falsch 
		Keywordseen.STRING=0;
		String identifier = yytext();
		return new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num,identifier));
            } 
            // fall through
          case 57: break;
          case 6: 
            { System.err.println("ERROR: unterminated string literal"+ yytext());
            } 
            // fall through
          case 58: break;
          case 7: 
            { CharNum.num += yytext().length();  // Erhöhe die Zeichenposition
            } 
            // fall through
          case 59: break;
          case 8: 
            { CharNum.num = 1;
            } 
            // fall through
          case 60: break;
          case 9: 
            { Symbol S = new Symbol(sym.DIVIDE, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 61: break;
          case 10: 
            { Symbol S = new Symbol(sym.TIMES, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 62: break;
          case 11: 
            { Symbol S = new Symbol(sym.PLUS, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 63: break;
          case 12: 
            { Symbol S = new Symbol(sym.SEMICOLON, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 64: break;
          case 13: 
            { Symbol S = new Symbol(sym.COLON, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 65: break;
          case 14: 
            { Symbol S = new Symbol(sym.ASSIGN, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 66: break;
          case 15: 
            { Symbol S = new Symbol(sym.NOT, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 67: break;
          case 16: 
            { Symbol S = new Symbol(sym.LESS, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 68: break;
          case 17: 
            { Symbol S = new Symbol(sym.GREATER, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 69: break;
          case 18: 
            { Symbol S = new Symbol(sym.COMMA, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 70: break;
          case 19: 
            { Symbol S = new Symbol(sym.LCURLY, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 71: break;
          case 20: 
            { Symbol S = new Symbol(sym.RCURLY, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 72: break;
          case 21: 
            { Symbol S = new Symbol(sym.LPAREN, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 73: break;
          case 22: 
            { Symbol S = new Symbol(sym.RPAREN, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 74: break;
          case 23: 
            { String original = yytext();
	String modified; 
	if(original.contains("\\n")||original.contains("\\t")||original.contains("\\\"")||original.contains("\\\'")){
		modified = original.replace("\\n", "\n").replace("\\t", "\t").replace("\\\"", "\"").replace("\\\'", "\'");
		
		return new Symbol(sym.STRINGLITERAL, new StringTokenVal(yyline + 1, CharNum.num,modified));

	}
				
	else{

        return new Symbol(sym.STRINGLITERAL, new StringTokenVal(yyline + 1, CharNum.num,yytext()));
    }
            } 
            // fall through
          case 75: break;
          case 24: 
            { Symbol S = new Symbol(sym.IF, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 76: break;
          case 25: 
            { Symbol S = new Symbol(sym.DO, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 77: break;
          case 26: 
            { /* Einzeiligen Kommentar ignorieren */
            } 
            // fall through
          case 78: break;
          case 27: 
            { System.err.println("ERROR: unclosed comment ");
            } 
            // fall through
          case 79: break;
          case 28: 
            { Symbol S = new Symbol(sym.POWER, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 80: break;
          case 29: 
            { Symbol S = new Symbol(sym.EQUALS, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 81: break;
          case 30: 
            { Symbol S = new Symbol(sym.NOTEQUALS, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 82: break;
          case 31: 
            { Symbol S = new Symbol(sym.LESSEQ, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 83: break;
          case 32: 
            { Symbol S = new Symbol(sym.GREATEREQ, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 84: break;
          case 33: 
            { Symbol S = new Symbol(sym.AND, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 85: break;
          case 34: 
            { Symbol S = new Symbol(sym.OR, new TokenVal(yyline+1, CharNum.num));
	    CharNum.num++;
	    return S;
            } 
            // fall through
          case 86: break;
          case 35: 
            { Keywordseen.INT = 1;
			Symbol S = new Symbol(sym.INT, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 87: break;
          case 36: 
            { System.err.println("ERROR: invalid escape character at string literal: "+ yytext()); 
			CharNum.num += yytext().length();
            } 
            // fall through
          case 88: break;
          case 37: 
            { Symbol S = new Symbol(sym.ELSE, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 89: break;
          case 38: 
            { Symbol S = new Symbol(sym.TRUE, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 90: break;
          case 39: 
            { Symbol S = new Symbol(sym.CASE, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 91: break;
          case 40: 
            { Symbol S = new Symbol(sym.VOID, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 92: break;
          case 41: 
            { /* ignoriert mehrzeilige Kommentare */
            } 
            // fall through
          case 93: break;
          case 42: 
            { Symbol S =  new Symbol(sym.WHILE, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 94: break;
          case 43: 
            { Symbol S = new Symbol(sym.FALSE, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 95: break;
          case 44: 
            { Symbol S = new Symbol(sym.PRINT, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 96: break;
          case 45: 
            { Symbol S = new Symbol(sym.CLASS, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 97: break;
          case 46: 
            { Symbol S = new Symbol(sym.SWITCH, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 98: break;
          case 47: 
            { Symbol S = new Symbol(sym.STATIC, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 99: break;
          case 48: 
            { Symbol S = new Symbol(sym.RETURN, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 100: break;
          case 49: 
            { Symbol S = new Symbol(sym.PUBLIC, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 101: break;
          case 50: 
            { Keywordseen.STRING=1;
			Symbol S = new Symbol(sym.STRING, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 102: break;
          case 51: 
            { Symbol S = new Symbol(sym.DEFAULT, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 103: break;
          case 52: 
            { Symbol S = new Symbol(sym.BOOLEAN, new TokenVal(yyline+1, CharNum.num)); 
			CharNum.num++;
			return S;
            } 
            // fall through
          case 104: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }


}
