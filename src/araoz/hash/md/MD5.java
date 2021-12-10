/*
 * Copyright (c) 2021
 * Fernando Enrique Araoz Morales.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package araoz.hash.md;

import araoz.hash.MD5Constantes;

import static araoz.hash.Utils.ROTL;

// https://datatracker.ietf.org/doc/html/rfc1321
// https://cse.unl.edu/~ssamal/crypto/genhash.php
public class MD5 extends MD {

    public MD5(String input) {
        super(input);
    }

    int constante(int i) {
        return MD5Constantes.constantes[i - 1];
    }

    int md5_cmn(int q, int a, int b, int x, int s, int t) {
        return b + ROTL(((a + q) + (x + t)), s);
    }

    /* Let [abcd k s i] denote the operation
          a = b + ((a + F(b,c,d) + X[k] + T[i]) <<< s).
     Do the following 16 operations. */
    int F1(int a, int b, int c, int d, int x, int s, int t, int[] bloque) {
        return md5_cmn((b & c) | ((~b) & d), a, b, bloque[x], s, constante(t));
    }

    /* Let [abcd k s i] denote the operation
          a = b + ((a + G(b,c,d) + X[k] + T[i]) <<< s).
     Do the following 16 operations. */
    int F2(int a, int b, int c, int d, int x, int s, int t, int[] bloque) {
        return md5_cmn((b & d) | (c & (~d)), a, b, bloque[x], s, constante(t));
    }

    /* Let [abcd k s t] denote the operation
          a = b + ((a + H(b,c,d) + X[k] + T[i]) <<< s).
     Do the following 16 operations. */
    int F3(int a, int b, int c, int d, int x, int s, int t, int[] bloque) {
        return md5_cmn(b ^ c ^ d, a, b, bloque[x], s, constante(t));
    }

    /* Let [abcd k s t] denote the operation
          a = b + ((a + I(b,c,d) + X[k] + T[i]) <<< s).
     Do the following 16 operations. */
    int F4(int a, int b, int c, int d, int x, int s, int t, int[] bloque) {
        return md5_cmn(c ^ (b | (~d)), a, b, bloque[x], s, constante(t));
    }

    void procesarBloque(int[] bloque, int[] H) {
        int A = H[0],
            B = H[1],
            C = H[2],
            D = H[3];

        A = F1(A, B, C, D, 0, 7, 1, bloque);
        D = F1(D, A, B, C, 1, 12, 2, bloque);
        C = F1(C, D, A, B, 2, 17, 3, bloque);
        B = F1(B, C, D, A, 3, 22, 4, bloque);
        A = F1(A, B, C, D, 4, 7, 5, bloque);
        D = F1(D, A, B, C, 5, 12, 6, bloque);
        C = F1(C, D, A, B, 6, 17, 7, bloque);
        B = F1(B, C, D, A, 7, 22, 8, bloque);
        A = F1(A, B, C, D, 8, 7, 9, bloque);
        D = F1(D, A, B, C, 9, 12, 10, bloque);
        C = F1(C, D, A, B, 10, 17, 11, bloque);
        B = F1(B, C, D, A, 11, 22, 12, bloque);
        A = F1(A, B, C, D, 12, 7, 13, bloque);
        D = F1(D, A, B, C, 13, 12, 14, bloque);
        C = F1(C, D, A, B, 14, 17, 15, bloque);
        B = F1(B, C, D, A, 15, 22, 16, bloque);

        A = F2(A, B, C, D, 1, 5, 17, bloque);
        D = F2(D, A, B, C, 6, 9, 18, bloque);
        C = F2(C, D, A, B, 11, 14, 19, bloque);
        B = F2(B, C, D, A, 0, 20, 20, bloque);
        A = F2(A, B, C, D, 5, 5, 21, bloque);
        D = F2(D, A, B, C, 10, 9, 22, bloque);
        C = F2(C, D, A, B, 15, 14, 23, bloque);
        B = F2(B, C, D, A, 4, 20, 24, bloque);
        A = F2(A, B, C, D, 9, 5, 25, bloque);
        D = F2(D, A, B, C, 14, 9, 26, bloque);
        C = F2(C, D, A, B, 3, 14, 27, bloque);
        B = F2(B, C, D, A, 8, 20, 28, bloque);
        A = F2(A, B, C, D, 13, 5, 29, bloque);
        D = F2(D, A, B, C, 2, 9, 30, bloque);
        C = F2(C, D, A, B, 7, 14, 31, bloque);
        B = F2(B, C, D, A, 12, 20, 32, bloque);

        A = F3(A, B, C, D, 5, 4, 33, bloque);
        D = F3(D, A, B, C, 8, 11, 34, bloque);
        C = F3(C, D, A, B, 11, 16, 35, bloque);
        B = F3(B, C, D, A, 14, 23, 36, bloque);
        A = F3(A, B, C, D, 1, 4, 37, bloque);
        D = F3(D, A, B, C, 4, 11, 38, bloque);
        C = F3(C, D, A, B, 7, 16, 39, bloque);
        B = F3(B, C, D, A, 10, 23, 40, bloque);
        A = F3(A, B, C, D, 13, 4, 41, bloque);
        D = F3(D, A, B, C, 0, 11, 42, bloque);
        C = F3(C, D, A, B, 3, 16, 43, bloque);
        B = F3(B, C, D, A, 6, 23, 44, bloque);
        A = F3(A, B, C, D, 9, 4, 45, bloque);
        D = F3(D, A, B, C, 12, 11, 46, bloque);
        C = F3(C, D, A, B, 15, 16, 47, bloque);
        B = F3(B, C, D, A, 2, 23, 48, bloque);

        A = F4(A, B, C, D, 0, 6, 49, bloque);
        D = F4(D, A, B, C, 7, 10, 50, bloque);
        C = F4(C, D, A, B, 14, 15, 51, bloque);
        B = F4(B, C, D, A, 5, 21, 52, bloque);
        A = F4(A, B, C, D, 12, 6, 53, bloque);
        D = F4(D, A, B, C, 3, 10, 54, bloque);
        C = F4(C, D, A, B, 10, 15, 55, bloque);
        B = F4(B, C, D, A, 1, 21, 56, bloque);
        A = F4(A, B, C, D, 8, 6, 57, bloque);
        D = F4(D, A, B, C, 15, 10, 58, bloque);
        C = F4(C, D, A, B, 6, 15, 59, bloque);
        B = F4(B, C, D, A, 13, 21, 60, bloque);
        A = F4(A, B, C, D, 4, 6, 61, bloque);
        D = F4(D, A, B, C, 11, 10, 62, bloque);
        C = F4(C, D, A, B, 2, 15, 63, bloque);
        B = F4(B, C, D, A, 9, 21, 64, bloque);

        H[0] += A;
        H[1] += B;
        H[2] += C;
        H[3] += D;
    }

    String run(int[][] bloques) {
        int[] estadoActual = {
            0x67452301,
            0xEFCDAB89,
            0x98BADCFE,
            0x10325476
        };

        return run(bloques, estadoActual);
    }

}
