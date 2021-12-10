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

import static araoz.hash.Utils.ROTL;

// https://link.springer.com/content/pdf/10.1007%2F3-540-38424-3_22.pdf
public class MD4 extends MD {

    public MD4(String input) {
        super(input);
    }

    int F1(int a, int b, int c, int d, int x, int s, int[] bloque) {
        return ROTL(a + ((b & c) | (~b & d)) + bloque[x], s);
    }

    int F2(int a, int b, int c, int d, int x, int s, int[] bloque) {
        return ROTL(a + ((b & c) | (b & d) | (c & d)) + bloque[x] + 0x5a827999, s);
    }

    int F3(int a, int b, int c, int d, int x, int s, int[] bloque) {
        return ROTL(a + (b ^ c ^ d) + bloque[x] + 0x6ed9eba1, s);
    }

    void procesarBloque(int[] bloque, int[] H) {
        int A = H[0],
            B = H[1],
            C = H[2],
            D = H[3];

        A = F1(A, B, C, D, 0, 3, bloque);
        D = F1(D, A, B, C, 1, 7, bloque);
        C = F1(C, D, A, B, 2, 11, bloque);
        B = F1(B, C, D, A, 3, 19, bloque);
        A = F1(A, B, C, D, 4, 3, bloque);
        D = F1(D, A, B, C, 5, 7, bloque);
        C = F1(C, D, A, B, 6, 11, bloque);
        B = F1(B, C, D, A, 7, 19, bloque);
        A = F1(A, B, C, D, 8, 3, bloque);
        D = F1(D, A, B, C, 9, 7, bloque);
        C = F1(C, D, A, B, 10, 11, bloque);
        B = F1(B, C, D, A, 11, 19, bloque);
        A = F1(A, B, C, D, 12, 3, bloque);
        D = F1(D, A, B, C, 13, 7, bloque);
        C = F1(C, D, A, B, 14, 11, bloque);
        B = F1(B, C, D, A, 15, 19, bloque);

        A = F2(A, B, C, D, 0, 3, bloque);
        D = F2(D, A, B, C, 4, 5, bloque);
        C = F2(C, D, A, B, 8, 9, bloque);
        B = F2(B, C, D, A, 12, 13, bloque);
        A = F2(A, B, C, D, 1, 3, bloque);
        D = F2(D, A, B, C, 5, 5, bloque);
        C = F2(C, D, A, B, 9, 9, bloque);
        B = F2(B, C, D, A, 13, 13, bloque);
        A = F2(A, B, C, D, 2, 3, bloque);
        D = F2(D, A, B, C, 6, 5, bloque);
        C = F2(C, D, A, B, 10, 9, bloque);
        B = F2(B, C, D, A, 14, 13, bloque);
        A = F2(A, B, C, D, 3, 3, bloque);
        D = F2(D, A, B, C, 7, 5, bloque);
        C = F2(C, D, A, B, 11, 9, bloque);
        B = F2(B, C, D, A, 15, 13, bloque);

        A = F3(A, B, C, D, 0, 3, bloque);
        D = F3(D, A, B, C, 8, 9, bloque);
        C = F3(C, D, A, B, 4, 11, bloque);
        B = F3(B, C, D, A, 12, 15, bloque);
        A = F3(A, B, C, D, 2, 3, bloque);
        D = F3(D, A, B, C, 10, 9, bloque);
        C = F3(C, D, A, B, 6, 11, bloque);
        B = F3(B, C, D, A, 14, 15, bloque);
        A = F3(A, B, C, D, 1, 3, bloque);
        D = F3(D, A, B, C, 9, 9, bloque);
        C = F3(C, D, A, B, 5, 11, bloque);
        B = F3(B, C, D, A, 13, 15, bloque);
        A = F3(A, B, C, D, 3, 3, bloque);
        D = F3(D, A, B, C, 11, 9, bloque);
        C = F3(C, D, A, B, 7, 11, bloque);
        B = F3(B, C, D, A, 15, 15, bloque);

        H[0] += A;
        H[1] += B;
        H[2] += C;
        H[3] += D;
    }

    String run(int[][] bloques) {
        int[] estadoActual = {
            0x67452301,
            0xefcdab89,
            0x98badcfe,
            0x10325476
        };

        return run(bloques, estadoActual);
    }

}
