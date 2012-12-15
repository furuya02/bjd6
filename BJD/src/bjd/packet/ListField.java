package bjd.packet;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Vector;

import bjd.util.ListBase;

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

	private IField search(String name) {
		for (IField a : ar) {
			if (a.getName().equals(name)) {
				return a;
			}
		}
		return null;
	}

	public boolean set(String name, byte[] val) {
		IField o = search(name);
		if (o != null && o instanceof OneField) {
			((OneField) o).set(val);
			return true;
		}
		return false;
	}

	public byte[] get(String name) {
		IField o = search(name);
		if (o != null && o instanceof OneField) {
			return ((OneField) o).get();
		}
		return null;
	}

	public short getShort(String name) throws InvalidObjectException {
		IField o = search(name);
		if (o != null && o instanceof OneField) {
			return ((OneField) o).getShort();
		}
		throw new InvalidObjectException(String.format("getShort(%s)", name));
	}

	public int getInt(String name) throws InvalidObjectException {
		IField o = search(name);
		if (o != null && o instanceof OneField) {
			return ((OneField) o).getInt();
		}
		throw new InvalidObjectException(String.format("getInt(%s)", name));
	}

	public byte getByte(String name) throws InvalidObjectException  {
		IField o = search(name);
		if (o != null && o instanceof OneField) {
			return ((OneField) o).getByte();
		}
		throw new InvalidObjectException(String.format("getByte(%s)", name));
	}

	public long getLong(String name) throws InvalidObjectException {
		IField o = search(name);
		if (o != null && o instanceof OneField) {
			return ((OneField) o).getLong();
		}
		throw new InvalidObjectException(String.format("getLong(%s)", name));
	}

}
