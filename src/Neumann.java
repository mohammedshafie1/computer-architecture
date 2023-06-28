import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Neumann {
	String pc = padLeftZeros("0", 32);
	static String[] Registers = new String[32];
	String[] memory = new String[2048];
	static instruction[] arrInst = new instruction[5];
	static int[] MaxCycles = { 1, 2, 2, 1, 1 };
	int top = 0;
	int totalc = 0;
	static HashMap<String, String> hm;

	{
		hm = new HashMap<String, String>();
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

	public Neumann() {
		for (int i = 0; i < Registers.length; i++) {
			Registers[i] = padLeftZeros("0", 32);
		}
		for (int i = 0; i < memory.length; i++) {
			memory[i] = padLeftZeros("0", 32);
		}

	}

	public static String center(String s, int size, char pad) {
		if (s == null || size <= s.length())
			return s;

		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < (size - s.length()) / 2; i++) {
			sb.append(pad);
		}
		sb.append(s);
		while (sb.length() < size) {
			sb.append(pad);
		}
		return sb.toString();
	}

	private void printChangeInMemory() {
		if (arrInst[3] == null)
			return;
		instruction curInstruction = arrInst[3];
		if (curInstruction.Opcode == 11) {
			System.out.println("\n***** Memory word at index " + curInstruction.toMemory + " was set to "
					+ instruction.parseSignedInteger(curInstruction.item) + " *****");
		}
	}

	private void printChangedRegisters() {
		List<Integer> changeArr = List.of(0, 1, 2, 3, 5, 6, 8, 9, 10);
		if (arrInst[4] == null || !changeArr.contains(arrInst[4].Opcode))
			return;
		instruction curInstruction = arrInst[4];
		// R0 cannot be overwritten
		if (curInstruction.dstreg == 0)
			System.out.println("\n***** R0 Cannot be overwritten *****");
		else
			System.out.println("\n***** R" + curInstruction.dstreg + " was set to "
					+ instruction.parseSignedInteger(Registers[curInstruction.dstreg]) + " *****");
	}

	public void printarrInst() {
		if (arrInst[0] == null)
			System.out.println("Fetching : " + "no instruction");
		else {
			System.out.println("Fetching : " + arrInst[0].stringInstruction);
			System.out.println("Inputs : ");
			System.out.println("PC = " + (Integer.parseInt(pc, 2) - 1));
			System.out.println("Outputs : ");
			System.out.println("BinaryInstruction = " + arrInst[0].binInstruction);
		}
		System.out.println("\n----------------------------\n");
		if (arrInst[1] == null)
			System.out.println("Decoding : " + "no instruction");
		else {
			System.out.println("Decoding : " + arrInst[1].stringInstruction);
			System.out.println("Inputs : ");
			System.out.println("BinaryInstruction = " + arrInst[1].binInstruction);
			System.out.println("Outputs : ");
			System.out.println("Opcode = " + arrInst[1].Opcode);
			if (arrInst[1].Opcode == 4) {
				System.out.println("R1 = " + arrInst[1].operandA);
				System.out.println("R2 = " + arrInst[1].operandB);
				System.out.println("R1 = " + arrInst[1].dstreg);
			} else {
				System.out.println("R1 = " + arrInst[1].dstreg);
				System.out.println("R2 = " + arrInst[1].operandA);
				System.out.println("R3 = " + arrInst[1].operandB);
			}
			System.out.println("shift amount = " + arrInst[1].shamt);
			System.out.println("immediate = " + arrInst[1].imm);
			System.out.println("address = " + instruction.parseSignedInteger(arrInst[1].Address));
			System.out.println("Registers[ " + arrInst[1].dstreg + " ] = " + arrInst[1].valueR1);
			System.out.println("Registers[ " + arrInst[1].operandA + " ] = " + arrInst[1].valueR2);
			System.out.println("Registers[ " + arrInst[1].operandB + " ] = " + arrInst[1].valueR3);
		}
		System.out.println("\n----------------------------\n");
		if (arrInst[2] == null)
			System.out.println("Executing : " + "no instruction");
		else {
			System.out.println("Executing : " + arrInst[2].stringInstruction);
			switch (arrInst[2].Opcode) {
			// add: R1 = R2 + R3
			case 0:
				// sub: R1 = R2 - R3
			case 1:
				// mul: R1 = R2 * R3
			case 2:
				System.out.println("Inputs : ");
				System.out.println("Registers[ " + arrInst[2].operandA + " ] = " + arrInst[2].valueR2);
				System.out.println("Registers[ " + arrInst[2].operandB + " ] = " + arrInst[2].valueR3);
				System.out.println("Outputs : ");
				System.out.println("ALU Result = " + instruction.parseSignedInteger(arrInst[2].res));
				break;
			// MOVI: R1 = IMM
			case 3:
				System.out.println("Inputs : ");
				System.out.println("Registers[ " + arrInst[2].dstreg + " ] = " + arrInst[2].valueR1);
				System.out.println("Outputs : ");
				System.out.println("ALU Result = " + instruction.parseSignedInteger(arrInst[2].res));
				break;
			// JEQ R1 R2 IMM
			// jump to PC+1+IMM no registers needed
			// pc is already pointing to next instruction,add imm to it
			case 4:
				System.out.println("Inputs : ");
				System.out.println("Registers[ " + arrInst[2].operandA + " ] = " + arrInst[2].valueR2);
				System.out.println("Registers[ " + arrInst[2].operandB + " ] = " + arrInst[2].valueR3);
				System.out.println("Outputs : ");
				System.out.println("Jump Address = " + instruction.parseSignedInteger(arrInst[2].res));
				break;
			// R1 = R2 & R3
			case 5:
				System.out.println("Inputs : ");
				System.out.println("Registers[ " + arrInst[2].operandA + " ] = " + arrInst[2].valueR2);
				System.out.println("Registers[ " + arrInst[2].operandB + " ] = " + arrInst[2].valueR3);
				System.out.println("Outputs : ");
				System.out.println("ALU Result = " + instruction.parseSignedInteger(arrInst[2].res));
				break;
			// R1 = R2 ⊕ IMM
			case 6:
				System.out.println("Inputs : ");
				System.out.println("Registers[ " + arrInst[2].operandA + " ] = " + arrInst[2].valueR2);
				System.out.println("imm = " + arrInst[2].imm);
				System.out.println("Outputs : ");
				System.out.println("ALU Result = " + instruction.parseSignedInteger(arrInst[2].res));
				break;
			// jump to address no registers needed
			case 7:
				System.out.println("Inputs : ");
				System.out.println("No inputs needed");
				System.out.println("Outputs : ");
				System.out.println("Jump address = " + instruction.parseSignedInteger(arrInst[2].res));
				break;
			// logical shift left:R1 = R2<<<shamt
			case 8:
				System.out.println("Inputs : ");
				System.out.println("Registers[ " + arrInst[2].operandA + " ] = " + arrInst[2].valueR2);
				System.out.println("shamt = " + arrInst[2].shamt);
				System.out.println("Outputs : ");
				System.out.println("ALU Result = " + instruction.parseSignedInteger(arrInst[2].res));
				break;
			// logical shift right: R1 = R2>>>shamt
			case 9:
				System.out.println("Inputs : ");
				System.out.println("Registers[ " + arrInst[2].operandA + " ] = " + arrInst[2].valueR2);
				System.out.println("shamt = " + arrInst[2].shamt);
				System.out.println("Outputs : ");
				System.out.println("ALU Result = " + instruction.parseSignedInteger(arrInst[2].res));
				break;
			// MOVR = R1= MEM[R2+IMM]
			case 10:
				System.out.println("Inputs : ");
				System.out.println("Registers[ " + arrInst[2].operandA + " ] = " + arrInst[2].valueR2);
				System.out.println("imm = " + arrInst[2].imm);
				System.out.println("Outputs : ");
				System.out.println("Memory Address = " + (arrInst[2].fromMemory));
				break;
			// MOVM = MEM[R2+IMM]=R1
			case 11:
				System.out.println("Inputs : ");
				System.out.println("Registers[ " + arrInst[2].operandA + " ] = " + arrInst[2].valueR2);
				System.out.println("imm = " + arrInst[2].imm);
				System.out.println("Outputs : ");
				System.out.println("Memory Address = " + (arrInst[2].toMemory));
				break;
			}
		}
		System.out.println("\n----------------------------\n");
		if (arrInst[3] == null)
			System.out.println("Memory Accessing : " + "no instruction");
		else {
			System.out.println("Memory Accessing : " + arrInst[3].stringInstruction);
			switch (arrInst[3].Opcode) {
			// MOVR = R1= MEM[R2+IMM]
			case 10:
				System.out.println("Inputs : ");
				System.out.println("Memory Address = " + (arrInst[3].fromMemory));
				System.out.println("Outputs : ");
				System.out.println(
						"Registers[ " + arrInst[3].dstreg + " ] = " + instruction.parseSignedInteger(arrInst[3].res));
				break;
			// MOVM = MEM[R2+IMM]=R1
			case 11:
				System.out.println("Inputs : ");
				System.out.println("Memory Address = " + arrInst[3].toMemory);
				System.out.println(
						"Registers[ " + arrInst[3].dstreg + " ] = " + instruction.parseSignedInteger(arrInst[3].item));
				System.out.println("Outputs : ");
				System.out.println("Memory [ " + arrInst[3].toMemory + " ] = "
						+ instruction.parseSignedInteger(memory[arrInst[3].toMemory]));
				break;
			default:
				System.out.println("No memory accessing is needed");
				break;
			}
		}
		System.out.println("\n----------------------------\n");
		if (arrInst[4] == null)
			System.out.println("Writing Back : " + "no instruction");
		else {
			System.out.println("Writing Back : " + arrInst[4].stringInstruction);
			System.out.println("Inputs : ");
			System.out.println("No inputs needed");
			System.out.println("Outputs : ");
			if (List.of(0, 1, 2, 3, 5, 6, 8, 9, 10).contains(arrInst[4].Opcode))
				System.out.println(
						"Registers[ " + arrInst[4].dstreg + " ] = " + instruction.parseSignedInteger(arrInst[4].res));
			else
				System.out.println("No write back");
		}
	}

	private void printAllMemory() {
		System.out.println("Memory content:");
		System.out.println("Memory Instructions Part:");
		int i = 0;
		System.out.println("-------------------------");
		while (i < 1023) {

			System.out.println("|" + center(i + "", 11, ' ') + "|" + center(memory[i] + "", 11, ' ') + "|");
			if (i != 1023)
				System.out.println("-------------------------");

			i++;
		}
		System.out.println("-------------------------");
		System.out.println();
		System.out.println("Memory Data Part:");
		while (i < 2048) {
			System.out.println("|" + center(i + "", 11, ' ') + "|" + center(memory[i] + "", 11, ' ') + "|");
			if (i != 1023)
				System.out.println("-------------------------");

			i++;
		}

//	        System.out.println("-----------------------") ;

		System.out.println("\n-----------------------------------------------------------------------\n");

	}

	private boolean allNull() {
		for (instruction i : arrInst) {
			if (i != null)
				return false;
		}
		return true;
	}

	public void load(String path) throws IOException {
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);

		int i = 0;
		while (br.ready()) {
			String line = br.readLine();
			if (!line.equals("")) {
			
				String[] arr = (line.toUpperCase().trim()).split(" ");
				String Binary = toBinary(arr);
				if (i <= 1023) {
					this.memory[i++] = Binary;
				}
			}
		}	totalc=i;

	}

	private static String toBinaryString(int number) {
		int numBits = 18; // Number of bits for the binary representation
		StringBuilder binary = new StringBuilder();

		// Convert to binary
		for (int i = numBits - 1; i >= 0; i--) {
			int mask = 1 << i;
			binary.append((number & mask) != 0 ? "1" : "0");
		}

		return binary.toString();
	}

	private static String padLeftZeros(String str, int length) {
		StringBuilder paddedBuilder = new StringBuilder(str);
		while (paddedBuilder.length() < length) {
			paddedBuilder.insert(0, '0');
		}
		return paddedBuilder.toString();
	}

	private static String toBinary(String[] arr) {
		String operation = arr[0];

		String one = hm.get(operation);

		String two = "";
		String three = "";
		String four = "";
		String five = "";
		if (operation.equals("JMP")) {
			int d = Integer.parseInt(arr[1]);
			two = String.format("%028d", Long.parseLong(Integer.toBinaryString(d)));

		} else if (instruction.Rbasic_inst.contains(operation)) {
			int d = Integer.parseInt(arr[1].substring(1));
			two = String.format("%05d", Long.parseLong(Integer.toBinaryString(d)));

			int c = Integer.parseInt(arr[2].substring(1));
			three = String.format("%05d", Long.parseLong(Integer.toBinaryString(c)));

			int x = Integer.parseInt(arr[3].substring(1));
			four = String.format("%05d", Long.parseLong(Integer.toBinaryString(x)));

			five = String.format("%013d", Long.parseLong("0")); // shamt

		} else if (instruction.shifts_inst.contains(operation)) {

			int d = Integer.parseInt(arr[1].substring(1));
			two = String.format("%05d", Long.parseLong(Integer.toBinaryString(d)));

			int c = Integer.parseInt(arr[2].substring(1));
			three = String.format("%05d", Long.parseLong(Integer.toBinaryString(c)));

			four = String.format("%05d", Long.parseLong("0"));

			int t = Integer.parseUnsignedInt(arr[3]);
			five = String.format("%013d", Long.parseLong(Integer.toBinaryString(t)));
		} else if (operation.equals("MOVI")) {
			int d = Integer.parseInt(arr[1].substring(1));
			two = String.format("%05d", Long.parseLong(Integer.toBinaryString(d)));

			three = String.format("%05d", 0);
			int c = Integer.parseInt(arr[2]);
			four = toBinaryString(c);

		} else {
			if(arr[1]!=null) {
			int d = Integer.parseInt(arr[1].substring(1));
			two = String.format("%05d", Long.parseLong(Integer.toBinaryString(d)));}
			if(arr[2]!=null) {
			int c = Integer.parseInt(arr[2].substring(1));
			three = String.format("%05d", Long.parseLong(Integer.toBinaryString(c)));}
if(arr.length==4) {
			int t = Integer.parseInt(arr[3]);
			four = toBinaryString(t);
}
		}
		return one + two + three + four + five;

	}

	public void Fetch() {
		System.out.println(totalc);
	if( Integer.parseInt(pc, 2)>(totalc-1))return;
		int oldpc = Integer.parseInt(pc, 2);
		instruction inst = new instruction();
		String instruction = memory[oldpc];
		arrInst[0] = inst;
System.out.println("Instruction to fetch"+instruction);
		inst.Fetch(instruction);
		int newpc = oldpc + 1;
		this.pc = padLeftZeros(Integer.toBinaryString(newpc), 32);

	}

	public void decode() {
		if (arrInst[1] == null)
			return;
		arrInst[1].decode(Registers, Integer.parseInt(pc, 2));
	}

	public void execute() {
		if (arrInst[2] == null)
			return;
		arrInst[2].execute(Registers, pc);
	}

	public void Memory() {
		if (arrInst[3] == null)
			return;
		arrInst[3].Memory(memory, Registers);
	}

	public static void WriteBack() {
		if (arrInst[4] == null)
			return;
		int Opcode = arrInst[4].Opcode;
		if (Opcode == 0 || Opcode == 1 || Opcode == 2 || Opcode == 3 || Opcode == 5 || Opcode == 6 || Opcode == 8
				|| Opcode == 9 || Opcode == 10) {
			if(arrInst[4].wb != 0) {
			Registers[arrInst[4].wb] = arrInst[4].res;}

		}
	}

	public void systemRun(String path) {
		try {
			load(path);
			System.out.println(memory[12]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int Ttime = 7 + ((this.totalc - 1) * 2);
		int clock = 1;
		System.out.println("totalc"+totalc);
		while (Integer.parseInt(pc,2)<totalc||!allNull()) {
			System.out.println("cycle number: " + clock);
			
			if (clock % 2 == 1) {
				
				Fetch();
				execute();
			} 
				WriteBack();
			
		
				Memory();
				decode();
			

			printarrInst();

			printChangedRegisters();
			printChangeInMemory();
			isJump();

			update();

			System.out.println("\n-----------------------------------------------------------------------\n");

			clock++;

		}

		printAllRegisters();

	}

	private void printAllRegisters() {
		System.out.println("Register File content:");
		int i = 0;
		for (String x : Registers) {
			System.out.println("Register: $R" + (i++) + " -> " + instruction.parseSignedInteger(x));
		}
		System.out.println("Register: $pc -> " + Integer.parseInt(pc,2));
		System.out.println("\n-----------------------------------------------------------------------\n");
	}

	private void isJump() {
		if (arrInst[3] == null)
			return;
		if (arrInst[3].jflag) {
			System.out
					.println("A jump happened and the instructions at the Decode and Execute stages will be dropped\n");

			pc = arrInst[3].res;
			arrInst[0] = null;
			arrInst[1] = null;
			arrInst[2] = null;
		}

	}

	private void update() {
		for (int i = 4; i >= 0; i--) {
			if (arrInst[i] != null) {
				arrInst[i].duration++;
			}
		}
		if (arrInst[4] != null) {
			if (arrInst[4].duration == MaxCycles[4]) {
				arrInst[4] = null;

			}
		}
		for (int i = 3; i >= 0; i--) {
			if ((arrInst[i] != null) && arrInst[i].duration == MaxCycles[i]) {

				try {
					arrInst[i + 1] = (instruction) arrInst[i].clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				arrInst[i + 1].duration = 0;
				arrInst[i] = null;

				if (i == 0) {
					arrInst[0] = null;

				}
			}

		}

	}
	private void printMemory() {
    	int i =0;
    	  while (i < 2048) {

              System.out.println("|"+ i+"|" + memory[i]+"|");
              if (i!=1023)
              System.out.println("-------------------------") ;
              else {  System.out.println("*******************************************") ;}
              i++;
          }
    }

	public static void main(String[] args) throws IOException {
		Neumann n = new Neumann();
		n.load("src\\program.txt");

	//	instruction.convertBinaryToAssembly("11111111111111100000000000000000"));
		//System.out.println(	"HEEEERE%%"+instruction.convertBinaryToAssembly("11111111111111100000000000000000"));
		n.systemRun("src\\program.txt");
		//n.printMemory();



	}
}