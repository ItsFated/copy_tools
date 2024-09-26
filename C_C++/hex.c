

const char HEX[] = {
        '0', '1', '2', '3',
        '4', '5', '6', '7',
        '8', '9', 'a', 'b',
        'c', 'd', 'e', 'f'
};
int hex2Digit(char c) {
    if (c >= '0' && c <= '9') {
        return c - '0';
    } else if (c >= 'a' && c <= 'f') {
        return 10 + c - 'a';
    } else if (c >= 'A' && c <= 'F') {
        return 10 + c - 'A';
    }
    return -1;
}
int bytes2Hex(const unsigned char* bytes, int bytesLen, char* output, int outputSize) {
    if (outputSize < bytesLen * 2 + 1) {
        return -1;
    }
    int i = 0;
    for (; i < bytesLen; i++) {
        output[i * 2] = HEX[(bytes[i] & 0xF0) >> 4];
        output[i * 2 + 1] = HEX[bytes[i] & 0xF];
    }
    output[bytesLen * 2] = '\0';
    return bytesLen * 2;
}
int hex2Bytes(const char* s, int sLen, unsigned char* output, int outputSize) {
    if (sLen % 2 != 0) {
        return -1;
    }
    if (outputSize < sLen / 2) {
        return -2;
    }
    int i = 0;
    for (; i < sLen; i += 2) {
        int highNibble = hex2Digit(s[i]);
        int lowNibble = hex2Digit(s[i + 1]);
        if (highNibble == -1 || lowNibble == -1) {
            return -3;
        }
        output[i / 2] = (highNibble << 4) | lowNibble;
    }
    return sLen / 2;
}