package bjd.plugins.ftp;

import bjd.option.Dat;
import bjd.option.OneDat;
import bjd.util.Crypt;
import bjd.util.ListBase;
import bjd.util.Util;

class ListUser extends ListBase<OneUser> {
	public ListUser(Dat dat) {
		if (dat != null) {
			for (OneDat o : dat) {
				//有効なデータだけを対象にする
				if (o.isEnable()) {
					FtpAcl ftpAcl = null;
					try {
						int n = Integer.parseInt(o.getStrList().get(0));
						ftpAcl = FtpAcl.valueOf(n);
					} catch (NumberFormatException e) {
						Util.runtimeException(this, e);
					}
					
					if (ftpAcl != null) {
						String homeDir = o.getStrList().get(1);
						String userName = o.getStrList().get(2);
						try {
							String password = Crypt.decrypt(o.getStrList().get(3));
							getAr().add(new OneUser(ftpAcl, userName, password, homeDir));
						} catch (Exception e) {
							Util.runtimeException(this, e);
						}
					}
				}
			}
		}
	}

	public OneUser get(String userName) {
		for (OneUser o : getAr()) {
			//Anonymousの場合、大文字小文字を区別しない
			if (userName.toUpperCase().equals("ANONYMOUS")) {
				if (o.getUserName().toUpperCase().equals(userName.toUpperCase())) {
					return o;
				}
			} else {
				if (o.getUserName().equals(userName)) {
					return o;
				}
			}
		}
		return null;
	}
}