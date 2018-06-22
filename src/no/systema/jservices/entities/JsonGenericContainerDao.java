package no.systema.jservices.entities;
import lombok.Data;
import java.util.*;
import no.systema.jservices.common.dao.GodsjfDao;

@Data
public class JsonGenericContainerDao {
	private String user;
	private Collection<GodsjfDao> list;
}
