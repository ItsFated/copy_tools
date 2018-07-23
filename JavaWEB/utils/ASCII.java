package notes.im_hero.com.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ASCII {
    /** 随机数字 */
    public static final byte TYPE_DIGIT        = 0b0001;
    /** 随机小写字母 */
    public static final byte TYPE_LOWERCASE    = 0b0010;
    /** 随机大写字母 */
    public static final byte TYPE_CAPITAL      = 0b0100;
    /** 随机标点符号 */
    public static final byte TYPE_PUNCTUATION  = 0b1000;

    public static final char NUL = 0;
    public static final char SOH = 1;
    public static final char STX = 2;
    public static final char ETX = 3;
    public static final char EOT = 4;
    public static final char ENQ = 5;
    public static final char ACK = 6;
    public static final char BEL = 7;
    public static final char BS  = 8;
    public static final char HT  = 9;
    public static final char LF  = 10;
    public static final char VT  = 11;
    public static final char FF  = 12;
    public static final char CR  = 13;
    public static final char SO  = 14;
    public static final char SI  = 15;
    public static final char DLE = 16;
    public static final char DC1 = 17;
    public static final char DC2 = 18;
    public static final char DC3 = 19;
    public static final char DC4 = 20;
    public static final char NAK = 21;
    public static final char SYN = 22;
    public static final char ETB = 23;
    public static final char CAN = 24;
    public static final char EM  = 25;
    public static final char SUB = 26;
    public static final char ESC = 27;
    public static final char FS  = 28;
    public static final char GS  = 29;
    public static final char RS  = 30;
    public static final char US  = 31;
    public static final char DEL = 127;
    public static final char SPACE = 32;

    public static final char[] DIGIT = new char[]{'0'/*48*/, '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static final char[] LOWERCASE_LETTERS = new char[]{
            'a' /*97,*/,
            'b' /*98,*/,
            'c' /*99,*/,
            'd' /*100,*/,
            'e' /*101,*/,
            'f' /*102,*/,
            'g' /*103,*/,
            'h' /*104,*/,
            'i' /*105,*/,
            'j' /*106,*/,
            'k' /*107,*/,
            'l' /*108,*/,
            'm' /*109,*/,
            'n' /*110,*/,
            'o' /*111,*/,
            'p' /*112,*/,
            'q' /*113,*/,
            'r' /*114,*/,
            's' /*115,*/,
            't' /*116,*/,
            'u' /*117,*/,
            'v' /*118,*/,
            'w' /*119,*/,
            'x' /*120,*/,
            'y' /*121,*/,
            'z' /*122,*/,
    };

    public static final char[] CAPITAL_LETTERS = new char[]{
            'A' /*65,*/,
            'B' /*66,*/,
            'C' /*67,*/,
            'D' /*68,*/,
            'E' /*69,*/,
            'F' /*70,*/,
            'G' /*71,*/,
            'H' /*72,*/,
            'I' /*73,*/,
            'J' /*74,*/,
            'K' /*75,*/,
            'L' /*76,*/,
            'M' /*77,*/,
            'N' /*78,*/,
            'O' /*79,*/,
            'P' /*80,*/,
            'Q' /*81,*/,
            'R' /*82,*/,
            'S' /*83,*/,
            'T' /*84,*/,
            'U' /*85,*/,
            'V' /*86,*/,
            'W' /*87,*/,
            'X' /*88,*/,
            'Y' /*89,*/,
            'Z' /*90,*/,
    };

    public static final char[] PUNCTUATION = new char[]{
            '!'  /*33*/,
            '"'  /*34*/,
            '#'  /*35*/,
            '$'  /*36*/,
            '%'  /*37*/,
            '&'  /*38*/,
            '\'' /*39*/,
            '('  /*40*/,
            ')'  /*41*/,
            '*'  /*42*/,
            '+'  /*43*/,
            ','  /*44*/,
            '-'  /*45*/,
            '.'  /*46*/,
            '/'  /*47*/,

            ':'  /*58*/,
            ';'  /*59*/,
            '<'  /*60*/,
            '='  /*61*/,
            '>'  /*62*/,
            '?'  /*63*/,

            '['  /*91*/,
            '\\' /*92*/,
            ']'  /*93*/,
            '^'  /*94*/,
            '_'  /*95*/,
            '`'  /*96*/,

            '{'  /*123*/,
            '|'  /*124*/,
            '}'  /*125*/,
            '~'  /*126*/
    };

    /**
     * <p>随机字符串</p>
     * @param length 长度
     * @param randomType 包含哪些字符集：{@link #TYPE_DIGIT}, {@link #TYPE_LOWERCASE}, {@link #TYPE_CAPITAL}, {@link #TYPE_PUNCTUATION}
     */
    public static String randomString(final int length, byte randomType) {
        randomType &= TYPE_DIGIT | TYPE_LOWERCASE | TYPE_CAPITAL | TYPE_PUNCTUATION;

        if (length > 0 && randomType > 0) {
            // 将字符集生成类型作为二维数组
            byte typesHas = Bit.count1(randomType);
            char[][] types = new char[typesHas][];
            byte totalCharCount = 0;
            byte totalCharCount0 = 0;
            byte totalCharCount1 = 0;
            byte totalCharCount2 = 0;
            byte totalCharCount3 = 0;

            for (int i = 0; i < typesHas; i++) {
                if ((randomType & TYPE_DIGIT) > 0) {
                    types[i] = DIGIT;
                    randomType &= ~TYPE_DIGIT;
                } else if ((randomType & TYPE_CAPITAL) > 0) {
                    types[i] = CAPITAL_LETTERS;
                    randomType &= ~TYPE_CAPITAL;
                } else if ((randomType & TYPE_LOWERCASE) > 0) {
                    types[i] = LOWERCASE_LETTERS;
                    randomType &= ~TYPE_LOWERCASE;
                } else if ((randomType & TYPE_PUNCTUATION) > 0) {
                    types[i] = PUNCTUATION;
                    randomType &= ~TYPE_PUNCTUATION;
                }
                switch (i) {
                    case 0: totalCharCount0 = (byte) (types[i].length); break;
                    case 1: totalCharCount1 = (byte) (types[i].length + totalCharCount0); break;
                    case 2: totalCharCount2 = (byte) (types[i].length + totalCharCount1); break;
                    case 3: totalCharCount3 = (byte) (types[i].length + totalCharCount2); break;
                }
                totalCharCount += types[i].length;
            }

            // 生成随机字符串
            Random random = ThreadLocalRandom.current();
            char[] string = new char[length];
            byte index;
            for (int i = 0; i < length; i++) {
                index = (byte) random.nextInt(totalCharCount);
                if (index < totalCharCount0) {
                    string[i] = types[0][index];
                } else if (index < totalCharCount1) {
                    string[i] = types[1][index - totalCharCount0];
                } else if (index < totalCharCount2) {
                    string[i] = types[2][index - totalCharCount1];
                } else if (index < totalCharCount3) {
                    string[i] = types[3][index - totalCharCount2];
                }
            }
            return String.valueOf(string);
        } else return "";
    }

    public static void main(String args[]) {
        int length = 99;
        System.out.println(randomString(length, TYPE_DIGIT));
        System.out.println(randomString(length, TYPE_CAPITAL));
        System.out.println(randomString(length, TYPE_LOWERCASE));
        System.out.println(randomString(length, TYPE_PUNCTUATION));

        System.out.println(randomString(length, (byte) (TYPE_PUNCTUATION|TYPE_DIGIT)));
        System.out.println(randomString(length, (byte) (TYPE_PUNCTUATION|TYPE_LOWERCASE)));
        System.out.println(randomString(length, (byte) (TYPE_PUNCTUATION|TYPE_CAPITAL)));
        System.out.println(randomString(length, (byte) (TYPE_LOWERCASE|TYPE_CAPITAL)));
        System.out.println(randomString(length, (byte) (TYPE_LOWERCASE|TYPE_DIGIT)));
        System.out.println(randomString(length, (byte) (TYPE_CAPITAL|TYPE_DIGIT)));

        System.out.println(randomString(length, (byte) (TYPE_CAPITAL|TYPE_DIGIT|TYPE_LOWERCASE)));
        System.out.println(randomString(length, (byte) (TYPE_CAPITAL|TYPE_DIGIT|TYPE_PUNCTUATION)));
        System.out.println(randomString(length, (byte) (TYPE_PUNCTUATION|TYPE_DIGIT|TYPE_LOWERCASE)));

        System.out.println(randomString(length, (byte) (TYPE_LOWERCASE|TYPE_DIGIT|TYPE_PUNCTUATION|TYPE_CAPITAL)));
    }
}
