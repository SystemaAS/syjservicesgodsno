package no.systema.jservices.entities;
import lombok.Data;
import java.util.*;
import no.systema.jservices.common.dao.GodsjfDao;
import no.systema.jservices.common.dao.IDao;

@Data
public class JsonGenericContainerDao<T> {
	private String user = "";
	private String errMsg = "";
	private Collection<T> list = new ArrayList<T>();
}
