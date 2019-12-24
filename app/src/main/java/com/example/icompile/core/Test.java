package com.example.icompile.core;

import java.io.*;
  
public class Test {  
  
public static void main(String args[]) {  
    // command args ignored  
    Parser parser = new Parser(new Scanner(  
    new DataInputStream(System.in)));  
    parser.run( );  
    System.out.println("done");  
} // main  
      
} // class Test 