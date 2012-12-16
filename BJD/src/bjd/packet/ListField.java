package bjd.packet;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Vector;

import bjd.util.ListBase;
import bjd.util.Util;

public final class ListField implements IField {

	private ArrayList<IField> ar = new ArrayList<IField>();
	private String name;

	public ListField(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void add(IField o) {
		ar.add(o);
	}

	private OneField search(String name) {
		for (IField a : ar) {
			if (a instanceof OneField) {
				if (a.getName().equals(name)) {
					return (OneField) a;
				}
			} else if (a instanceof ListField) {
				return ((ListField) a).search(name); //再帰処理
			} else {
				Util.runtimeException("ListField.search() arに、OneField及びListField以外が挿入されている");
			}
		}
		return null; //見つからない場合
	}

	public boolean set(String name, byte[] val) {
		OneField o = search(name);
		if (o != null) {
			o.set(val);
			return true;
		}
		return false;
	}

	public boolean setShort(String name, short val) {
		byte[] buf = { (byte) (val >> 8), (byte) val };
		return set(name, buf);
	}

	public byte[] get(String name) {
		OneField o = search(name);
		if (o != null) {
			return o.get();
		}
		return null;
	}

	public short getShort(String name) throws InvalidObjectException {
		OneField o = search(name);
		if (o != null) {
			return o.getShort();
		}
		throw new InvalidObjectException(String.format("getShort(%s)", name));
	}

	public int getInt(String name) throws InvalidObjectException {
		OneField o = search(name);
		if (o != null) {
			return o.getInt();
		}
		throw new InvalidObjectException(String.format("getInt(%s)", name));
	}

	public byte getByte(String name) throws InvalidObjectException {
		OneField o = search(name);
		if (o != null) {
			return o.getByte();
		}
		throw new InvalidObjectException(String.format("getByte(%s)", name));
	}

	public long getLong(String name) throws InvalidObjectException {
		OneField o = search(name);
		if (o != null) {
			return o.getLong();
		}
		throw new InvalidObjectException(String.format("getLong(%s)", name));
	}

	public int length() {
		int c = 0;
		for (IField a : ar) {
			if (a instanceof OneField) {
				c += ((OneField) a).getSize();
			} else if (a instanceof ListField) {
				c += ((ListField) a).length(); //再帰処理
			} else {
				Util.runtimeException("ListField.length() arに、OneField及びListField以外が挿入されている");
			}
		}
		return c;
	}

}
