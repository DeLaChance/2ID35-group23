package c1p;

import hist.PositionList;
import java.util.ArrayList;

/**
 *
 * @author francois
 */
public class C1PMatrix extends PositionList<C1PRow> {
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(C1PRow row : this)
		{
			for(int i = 0; i < row.getX(); i++) sb.append("0");
			for(int i = row.getX(); i < row.getY(); i++) sb.append("1");
			for(int i = row.getY(); i < this.getMaxY(); i++) sb.append("0");
			sb.append("\r\n");
		}
		return sb.toString();
	}
}
