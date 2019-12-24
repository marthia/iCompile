package com.example.icompile.core;/*
	Program to implement Recursive Descent Parser in Java
	Author: Manav Sanghavi		Author Link: https://www.facebook.com/manav.sanghavi 
	www.pracspedia.com
	
	Grammar:
	E -> x + T
	T -> (E)
	T -> x
*/

import java.util.*;

class RecursiveDescentParser {
	private static int ptr;
	private static char[] input;
	
	public static void main(String[] args) {
		System.out.println("Enter the input string:");
		String s = new Scanner(System.in).nextLine();
		input = s.toCharArray();
		if(input.length < 2) {
			System.out.println("The input string is invalid.");
			System.exit(0);
		}
		ptr = 0;
		boolean isValid = E();
		if((isValid) & (ptr == input.length)) {
			System.out.println("The input string is valid.");
		} else {
			System.out.println("The input string is invalid.");
		}
	}
	
	private static boolean E() {
		// Check if 'ptr' to 'ptr+2' is 'x + T'
		int fallback = ptr;
		if(input[ptr++] != 'x') {
			ptr = fallback;
			return false;
		}
		if(input[ptr++] != '+') {
			ptr = fallback;
			return false;
		}
		if(!T()) {
			ptr = fallback;
			return false;
		}
		return true;
	}
	
	private static boolean T() {
		// Check if 'ptr' to 'ptr+2' is '(E)' or if 'ptr' is 'x'
		int fallback = ptr;
		if(input[ptr] == 'x') {
			ptr++;
			return true;
		}
		else {
			if(input[ptr++] != '(') {
				ptr = fallback;
				return false;
			}
			if(!E()) {
				ptr = fallback;
				return false;
			}
			if(input[ptr++] != ')') {
				ptr = fallback;
				return false;
			}
			return true;
		}
	}
}