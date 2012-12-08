

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import bjd.Kernel;

public class ttt {

	
	@Test
	public void test() {
		Kernel kernel = new Kernel();
		String s = kernel.getProgDir();
		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		
		
		
		
		//TODO Debug Print
System.out.println(String.format("%s",currentDir));
	}

}
