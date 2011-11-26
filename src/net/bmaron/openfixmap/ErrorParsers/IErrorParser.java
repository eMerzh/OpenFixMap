package net.bmaron.openfixmap.ErrorParsers;

import java.util.List;
import net.bmaron.openfixmap.ErrorItem;
import org.osmdroid.util.BoundingBoxE6;

public interface IErrorParser {
	
	public void parse(int eLevel);
	
	public List<ErrorItem> getItems();

	public BoundingBoxE6 getBoundingBox() ;

	public void setBoundingBox(BoundingBoxE6 boundingBox);
}
