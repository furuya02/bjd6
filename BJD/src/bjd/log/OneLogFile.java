package bjd.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import bjd.util.IDispose;

/**
 * 生成時に１つのファイルをオープンしてset()で１行ずつ格納するクラス
 * 
 * @author SIN
 *
 */
final class OneLogFile implements IDispose {

    private FileWriter fw;
    private File file;
    
    /**
     * 
     * @param fileName 保存ファイル名
     * @throws IOException 
     */
	public OneLogFile(String fileName) throws IOException {
        file = new File(fileName);
        try {
        	// 追加モード
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			fw = null; //初期化失敗(事後使用不能)
			throw e;
		} 
    }

    @Override
    public void dispose() {
		if (fw == null) {
    		return;
    	}
        try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void set(String str) throws IOException {
		if (fw == null) {
			return;
		}
    	fw.write(str);
		fw.write("\r\n");
		fw.flush();
	}
    
	public String getPath() {
    	return file.getPath(); 
    }
}

