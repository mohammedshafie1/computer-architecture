
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class instruction implements Cloneable {
	String item = "0";
	static String[] Rbasic = { "ADD", "SUB", "MUL", "AND" };
	static HashSet<String> Rbasic_inst = new HashSet<String>(Arrays.asList(Rbasic));

	static String[] shifts = { "LSL", "LSR" };
	static HashSet<String> shifts_inst = new HashSet<String>(Arrays.asList(shifts));

	static String[] Itype = { "MOVI", "JEQ", "MOVR", "MOVM", "XORI" };
	static HashSet<String> I_inst = new HashSet<String>(Arrays.asList(Itype));
	public String res = "0";
	int duration;
	int valueR1 = 0;
	int valueR2 = 0;
	int valueR3 = 0;
	int imm = 0;
	int jpc=0;
	 HashMap<String, String> hm;

	{hm = new HashMap<String, String>();
		hm.put("ADD", "0000");

		hm.put("SUB", "0001");
		hm.put("MUL", "0010");
		hm.put("MOVI", "0011");
		hm.put("JEQ", "0100");
		hm.put("AND", "0101");
		hm.put("XORI", "0110");
		hm.put("JMP", "0111");
		hm.put("LSL", "1000");
		hm.put("LSR", "1001");
		hm.put("MOVR", "1010");
		hm.put("MOVM", "1011");
	}

	int toMemory = 0;
	int fromMemory = 0;

	int Opcode = 0;
	int shamt =0;
	
	String Address = "0";

	String stringInstruction = "";
	String binInstruction = "";
	int operandA = 0;
	int operandB = 0;
	int hopAmount = 0;

	int dstreg = 0;
	int wb = 0;
	boolean jflag = false;

	public instruction() {

		duration = 0;

	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

//	public static void print() {
//
//		System.out.println("binInstruction : " + convertBinaryToAssembly(binInstruction));
//		System.out.println("Opcode : " + Opcode);
//		System.out.println("operandA : " + operandA);
//		System.out.println("oprandB : " + operandB);
//		System.out.println("dstreg : " + dstreg);
//		System.out.println("hopAmount : " + hopAmount);
//
//	}

	public static String convertBinaryToAssembly(String inst) {
		// Rtype
		int opcode = Integer.parseInt(inst.substring(0, 4), 2);
		int r1 = Integer.parseInt(inst.substring(4, 9), 2);
		int r2 = Integer.parseInt(inst.substring(9, 14), 2);
		int r3 = Integer.parseInt(inst.substring(14, 19), 2);
		int shamt = Integer.parseInt(inst.substring(19, 32), 2);

		// Immediate
		int imm = parseSignedInteger(inst.substring(14, 32));

		// Jtype
		int address = Integer.parseInt(inst.substring(4, 32), 2);
		switch (opcode) {
		// add: R1 = R2 + R3
		case 0:
			return ("ADD " + "R" + r1 + " R" + r2 + " R" + r3);
		// sub: R1 = R2 - R3
		case 1:
			return ("SUB " + "R" + r1 + " R" + r2 + " R" + r3);
		// mul: R1 = R2 * R3
		case 2:
			return ("MUL " + "R" + r1 + " R" + r2 + " R" + r3);
		// MOVI: R1 = IMM
		case 3:
			return ("MOVI " + "R" + r1 + " " + imm);
		// jump to PC+1+IMM no registers needed
		// pc is already pointing to next binInstruction,add imm to it
		case 4:
			return ("JEQ " + "R" + r1 + " R" + r2 + " " + imm);
		// R1 = R2 & R3
		case 5:
			return ("AND " + "R" + r1 + " R" + r2 + " R" + r3);
		// R1 = R2 ⊕ IMM
		case 6:
			return ("XORI " + "R" + r1 + " R" + r2 + " " + imm);
		// jump to address no registers needed
		case 7:
			return ("JMP " + address);
		// logical shift left:R1 = R2<<<shamt
		case 8:
			return ("LSL " + "R" + r1 + " R" + r2 + " " + shamt);
		// logical shift right: R1 = R2>>>shamt
		case 9:
			return ("LSR " + "R" + r1 + " R" + r2 + " " + shamt);
		// MOVR = R1= MEM[R2+IMM]
		case 10:
			return ("MOVR " + "R" + r1 + " R" + r2 + " " + imm);
		// MOVM = MEM[R2+IMM]=R1
		case 11:
			return ("MOVM " + "R" + r1 + " R" + r2 + " " + imm);
		default:
			return "";
		}
	}

	public static int parseSignedInteger(String binString) {
		// if it's positive, just parse
		if (binString.charAt(0) == '0')
			return Integer.parseInt(binString, 2);

		// it's a negative number, flip bits and add one
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < binString.length(); i++)
			sb.append(binString.charAt(i) == '0' ? '1' : '0');
		int val = Integer.parseInt(sb.toString(), 2) + 1;
		return -val;
	}

	public void Fetch(String inst) {
		this.binInstruction = inst;
		
		this.stringInstruction = convertBinaryToAssembly(padLeftZeros(inst,32));
	}

	private static String padLeftZeros(String str, int length) {
		StringBuilder paddedBuilder = new StringBuilder(str);
		while (paddedBuilder.length() < length) {
			paddedBuilder.insert(0, '0');
		}
		return paddedBuilder.toString();
	}

	public void decode(String Register[],int pc) {
		System.out.println("Instruction to decode "+binInstruction);
		this.Opcode = Integer.parseInt(this.binInstruction.substring(0, 4), 2);
		switch (Opcode) {
		case (0):
		case (1):
		case (2):

		case (5):
	
			dstreg = Integer.parseInt(binInstruction.substring(4, 9), 2);
			operandA = Integer.parseInt(binInstruction.substring(9, 14), 2);
			operandB = Integer.parseInt(binInstruction.substring(14, 19), 2);
			wb = dstreg;

			break;
		case (3):
			dstreg = Integer.parseInt(binInstruction.substring(4, 9), 2);
			imm = parseSignedInteger(binInstruction.substring(14));
			wb = dstreg;

			break;
		case (4):
			operandA = Integer.parseInt(binInstruction.substring(4, 9), 2);
			operandB = Integer.parseInt(binInstruction.substring(9, 14), 2);
		System.out.println();
			//imm = parseSignedInteger(binInstruction.substring(14));
			jpc=pc;
			wb = dstreg;

			break;
		case (6):
			operandA = Integer.parseInt(binInstruction.substring(9, 14), 2);
			imm = parseSignedInteger(binInstruction.substring(14));
			dstreg = Integer.parseInt(binInstruction.substring(4, 9), 2);
			wb = dstreg;

			break;
		case (7):

			Address = binInstruction.substring(4);
			wb = dstreg;

			break;
		case (8):
		case (9):
			dstreg = Integer.parseInt(binInstruction.substring(4, 9), 2);

			operandA = Integer.parseInt(binInstruction.substring(9, 14), 2);
			shamt = Integer.parseInt(binInstruction.substring(19), 2);
			wb = dstreg;

			break;

		case (10):
		case (11):
			dstreg = Integer.parseInt(binInstruction.substring(4, 9), 2);

			operandA = Integer.parseInt(binInstruction.substring(9, 14), 2);
			imm = parseSignedInteger(binInstruction.substring(14));
			wb = dstreg;

			break;

		}
		valueR1 = parseSignedInteger(Register[dstreg]);
		valueR2 = parseSignedInteger(Register[operandA]);
		valueR3 = parseSignedInteger(Register[operandB]);

	}

	public void execute(String Registers[], String pc) {
		valueR1 = parseSignedInteger(Registers[dstreg]);
		valueR2 = parseSignedInteger(Registers[operandA]);
		valueR3 = parseSignedInteger(Registers[operandB]);

		switch (Opcode) {

		case (0):
			res = padLeftZeros(Integer.toBinaryString(
					parseSignedInteger(Registers[operandA]) + parseSignedInteger(Registers[operandB])), 32);
			break;
		case (1):
			res = padLeftZeros(Integer.toBinaryString(
					parseSignedInteger(Registers[operandA]) - parseSignedInteger(Registers[operandB])), 32);
			break;
		case (2):
			res = padLeftZeros(Integer.toBinaryString(
					parseSignedInteger(Registers[operandA]) * parseSignedInteger(Registers[operandB])), 32);
			break;
		case (3):
			res = padLeftZeros(Integer.toBinaryString(imm), 32);
			break;

		case (4):				res = padLeftZeros(Integer.toBinaryString(jpc + imm+1), 32);

			if (Registers[operandA].equals(Registers[operandB])) {
				jflag = true;
			}
			break;// /
		case (5):
			res = padLeftZeros(Integer.toBinaryString(
					parseSignedInteger(Registers[operandA]) & parseSignedInteger(Registers[operandB])), 32);
			break;
		case (6):
			res = padLeftZeros(Integer.toBinaryString(parseSignedInteger(Registers[operandA]) ^ imm), 32);
			break;
		case (7):
			jflag = true;
			res = pc.substring(0, 4) + Address;
			break;

		case (8):
			res = padLeftZeros(Integer.toBinaryString(parseSignedInteger(Registers[operandA]) << shamt), 32);
			break;
		case (9):
			res = padLeftZeros(Integer.toBinaryString(parseSignedInteger(Registers[operandA]) >> shamt), 32);
			break;
		case (10):
			System.out.println("operandA"+operandA);
			fromMemory = parseSignedInteger(Registers[operandA]) + imm ;
			break;
		case (11):
			toMemory = parseSignedInteger(Registers[operandA]) + imm ;
			break;
			default:res="0";
		}
		

	}

	public void Memory(String[] memory, String Registers[]) {
		if (Opcode == 10) {
			res = memory[fromMemory];
		}
		if (Opcode == 11) {
			item = Registers[dstreg];
			System.out.println("tomemory"+toMemory);
			memory[toMemory] = item;
		}
	}
//
//	public void WriteBack(String Registers[]) {
//		if (Opcode == 0 || Opcode == 1 || Opcode == 2 || Opcode == 3 || Opcode == 5 || Opcode == 6 || Opcode == 8
//				|| Opcode == 9 || Opcode == 10) {
//			Registers[wb] = res;
//
//		}
//	}

	public static void main(String[] args) throws IOException {
	}
}
