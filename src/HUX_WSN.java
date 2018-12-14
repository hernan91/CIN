import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.operator.binary.HUX;
import org.moeaframework.core.variable.BinaryVariable;

public class HUX_WSN extends HUX {
	private final double probability;

	public HUX_WSN(double probability) {
		super(probability);
		this.probability = probability;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result1 = parents[0].copy();
		Solution result2 = parents[1].copy();

		for (int i = 0; i < result1.getNumberOfVariables(); i++) {
			Variable variable1 = result1.getVariable(i);
			Variable variable2 = result2.getVariable(i);

			if ((PRNG.nextDouble() <= this.probability)
					&& (variable1 instanceof BinaryVariable)
					&& (variable2 instanceof BinaryVariable)) {
				evolve((BinaryVariable)variable1, (BinaryVariable)variable2);
			}
		}
		return new Solution[] { result1, result2 };
	}
}
