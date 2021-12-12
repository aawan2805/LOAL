/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa;
import java.awt.Point;

/**
 *
 * @author Abdullah Bashir Yasmin, Mario Konstanty Kochan Chmielik
 */
/* Decompiler 41ms, total 195ms, lines 62 */

public class ZobristHashing {
   private int hash;
   private final int[][] bitString;
   private int posiciones;

   public ZobristHashing(int pieces, int positions) {
      int pos2x = positions * positions;
      this.posiciones = positions;
      this.bitString = new int[pieces][pos2x];

      for(int i = 0; i < pieces; ++i) {
         for(int j = 0; j < pos2x; ++j) {
            this.bitString[i][j] = (int)((long)(Math.random() * Long.MAX_VALUE) & -1L);
         }
      }

      this.hash = 0;
   }

   public ZobristHashing(ZobristHashing zh) {
      this.bitString = zh.bitString;
      this.hash = zh.hash;
   }

   public void reset() {
      this.hash = 0;
   }

   private int A(int var1, int var2) {
      return this.xor(var1, var2);
   }

   public int add(CellType piece, Point position) {
      return this.xor(CellType.toColor01(piece), position.x + position.y * this.posiciones);
   }

   private int B(int var1, int var2) {
      return this.xor(var1, var2);
   }

   public int remove(CellType piece, Point position) {
      return this.xor(CellType.toColor01(piece), position.x + position.y * this.posiciones);
   }

   public int xor(int piece, int position) {
      this.hash ^= this.bitString[piece][position];
      return this.hash;
   }

   public void set(int hash) {
      this.hash = hash;
   }

   public int hashCode() {
      return this.hash;
   }
}