package com.example.travelleronline.util;

import java.util.Random;

public abstract class TokenCoder {

    private static final int LEFT_LIMIT = 97; // letter 'a'
    private static final int RIGHT_LIMIT = 122; // letter 'z'
    private static final int INT_TO_CHAR_START = 107; // letter 'k'
    private static final int FIRST_SPECIAL_LETTER = 100; // letter 'd'
    private static final int SECOND_SPECIAL_LETTER = 104; // letter 'h'
    private static final int BEGINNING_LENGTH = 11;
    private static final int FIRST_SPECIAL_POSITION_AT_BEGINNING = 5;
    private static final int SECOND_SPECIAL_POSITION_AT_BEGINNING = 9;
    private static final int ENDING_LENGTH = 13;
    private static final int FIRST_SPECIAL_POSITION_AT_ENDING = 2;
    private static final int SECOND_SPECIAL_POSITION_AT_ENDING = 8;
    // Positions start with 0!


    // PATTERN: <random_letters_string><encoded_user_id><random_letters_string>
    // beginning and ending have specific length and contain two "special" letters
    public static String encode(int uid) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BEGINNING_LENGTH; i++) {
            if (i == FIRST_SPECIAL_POSITION_AT_BEGINNING) {
                builder.append(randomlyCapitalize((char) FIRST_SPECIAL_LETTER));
            }
            else if (i == SECOND_SPECIAL_POSITION_AT_BEGINNING) {
                builder.append(randomlyCapitalize((char) SECOND_SPECIAL_LETTER));
            }
            else {
                builder.append(randomlyCapitalize(getRandomLetter()));
            }
        }

        builder.append(getEncodedInt(uid));

        for (int i = 0; i < ENDING_LENGTH; i++) {
            if (i == FIRST_SPECIAL_POSITION_AT_ENDING) {
                builder.append(randomlyCapitalize((char) FIRST_SPECIAL_LETTER));
            }
            else if (i == SECOND_SPECIAL_POSITION_AT_ENDING) {
                builder.append(randomlyCapitalize((char) SECOND_SPECIAL_LETTER));
            }
            else {
                builder.append(randomlyCapitalize(getRandomLetter()));
            }
        }
        return builder.toString();
    }

    // returns 0 when token is wrong
    public static int decode(String token) {
        if (!token.toLowerCase().matches("[a-z]")) {
            return 0;
        }
        int length = token.length();
        int firstSpecPosition = FIRST_SPECIAL_POSITION_AT_BEGINNING;
        int secondSpecPosition = SECOND_SPECIAL_POSITION_AT_BEGINNING;
        int thirdSpecPosition = length - ENDING_LENGTH + FIRST_SPECIAL_POSITION_AT_ENDING;
        int forthSpecPosition = length - ENDING_LENGTH + SECOND_SPECIAL_POSITION_AT_ENDING;
        if (token.charAt(firstSpecPosition) != (char) FIRST_SPECIAL_LETTER ||
                token.charAt(secondSpecPosition) != (char) SECOND_SPECIAL_LETTER ||
                token.charAt(thirdSpecPosition) != (char) FIRST_SPECIAL_LETTER ||
                token.charAt(forthSpecPosition) != (char) SECOND_SPECIAL_LETTER) {
            return 0;
        }

        int uidBeginIndex = BEGINNING_LENGTH;
        int uidEndIndex = length - ENDING_LENGTH;
        return getDecodedString(token.substring(uidBeginIndex, uidEndIndex));
    }

    private static char getRandomLetter() {
        return (char) (new Random().nextInt(RIGHT_LIMIT - LEFT_LIMIT + 1) + LEFT_LIMIT);
    }

    private static char randomlyCapitalize(char letter) {
        boolean chance = new Random().nextBoolean();
        if (chance) {
            return (char) (letter - 32);
        }
        else {
            return letter;
        }
    }

    private static String getEncodedInt(int integer) {
        StringBuilder original = new StringBuilder(Integer.toString(integer));
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < original.length(); i++) {
            char newFirstLetter = (char)
                    (Integer.parseInt(original.substring(0,1)) + INT_TO_CHAR_START);
            result.append(randomlyCapitalize(newFirstLetter));
            original.delete(0,1);
        }
        return result.toString();
    }

    private static int getDecodedString(String string) {
        StringBuilder original = new StringBuilder(string.toLowerCase());
        int result = 0;
        int digitWeight = 1;
        for (int i = 0; i < original.length(); i++) {
            int lastLetterValue = original.charAt(original.length() - 1);
            int digit = lastLetterValue - INT_TO_CHAR_START;
            if (digit < 0 || digit > 9) {
                return 0;
            }
            result = digit * digitWeight;
            digitWeight *= 10;
        }
        return result;
    }

}