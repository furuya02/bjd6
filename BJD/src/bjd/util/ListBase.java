package bjd.util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * オリジナルのListクラスを生成する場合の基底クラス<br>
 * Tの指定するクラスは IDisposableの制約がある<br>
 * 
 * @author user1
 *
 * @param <T>
 */
public abstract class ListBase<T extends IDisposable> implements Iterable<T>,
        Iterator<T> {
    private ArrayList<T> ar = new ArrayList<T>();
    private int index;

    public ListBase() {
        index = 0;
    }
    
    protected final ArrayList<T> getAr() {
    	return ar;
    }

    public final void dispose() {
        for (T o : ar) {
            o.dispose(); // 終了処理
        }
        ar.clear(); // 破棄
    }

    public final int size() {
        return ar.size();
    }

    public final void remove(int index) {
        ar.remove(index);
    }

    @Override
    public final Iterator<T> iterator() {
        index = 0;
    	return this;
    }

    @Override
    public final boolean hasNext() {
        return index < ar.size();
    }

    @Override
    public final T next() {
        T o = ar.get(index);
        index++;
        return o;
    }

    @Override
    public final void remove() {
    	throw new UnsupportedOperationException("ListBase.java remote()は未実装");
    }
}
