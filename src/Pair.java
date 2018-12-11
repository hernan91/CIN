import java.io.Serializable;

/**
 * @author francis
 * @date 01/12/2008
 *
 */
public final class Pair<F, S> implements Serializable, Comparable {
	private F first;
	private S second;

	public Pair(F f, S s) {
		first = f;
		second = s;
	}

	public F getFirst() {
		return first;
	}

	public void setFirst(F first) {
		this.first = first;
	}

	public S getSecond() {
		return second;
	}

	public void setSecond(S second) {
		this.second = second;
	}

	public String toString() {
		return "(" + first + "," + second + ")";
	}

	public boolean equals(Object o) {
		if (o instanceof Pair) {
			Pair p = (Pair) o;

			return first.equals(p.first) && second.equals(p.second);
		} else {
			return false;
		}

	}

	public int hashCode() {
		return first.hashCode() + second.hashCode();
	}

	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		S s = ((Pair<F, S>) arg0).second;
		Double fd1 = ((Double) s);
		Double fd2 = ((Double) this.second);

		if (fd1 < fd2)
			return 1;
		else if (fd1 > fd2)
			return -1;
		else
			return 0;
	}

}
