package de.ugoe.cs.tcs.simparameter.commitinfo;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Maps;
import de.ugoe.cs.tcs.simparameter.model.SmallCommit;
import de.ugoe.cs.tcs.simparameter.util.DatabaseContext;
import de.ugoe.cs.tcs.simparameter.util.MutableInt;
import de.ugoe.cs.tcs.simparameter.util.Parameter;
import de.ugoe.cs.tcs.simparameter.persons.DeveloperRole;
import de.ugoe.cs.tcs.simparameter.persons.DeveloperType;
import de.ugoe.cs.tcs.simparameter.persons.Identity;
import de.ugoe.cs.tcs.simparameter.persons.PersonInformation;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class CommitsPerDevType {
  private Logger logger = (Logger) LoggerFactory.getLogger("de.ugoe.cs.tcs.simparameter.commitinfo.CommitsPerDevType");
  private Map<DeveloperRole, MutableInt> numberOfCommitsPerRole;
  private Map<DeveloperType, MutableInt> numberOfCommitsPerType;
  private DatabaseContext ctx;

  public CommitsPerDevType() {
    numberOfCommitsPerRole = Maps.newHashMap();
    numberOfCommitsPerType = Maps.newHashMap();
    ctx = DatabaseContext.getInstance();
  }

  public void exportCSV() throws IOException {
    //TODO: consider problems (memory) with large projects
    //TODO: get sorted commit list from database
    List<SmallCommit> commits = ctx.getAllSmallCommits();
    commits.sort(Comparator.comparing(SmallCommit::getAuthorDate));
    if (commits.size() == 0) {
      logger.warn("No commits found!");
      return;
    }

    //TODO: change used calendar lib
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(commits.get(0).getAuthorDate());
    int exportYear = calendar.get(Calendar.YEAR);

    StringBuilder filename = new StringBuilder();
    filename.append("NoC_");
    filename.append(ctx.getProject().getName());
    String pathFile = Parameter.getInstance().getOutputFolder() + java.io.File.separator + filename.toString() + ".csv";
    PrintWriter pw = new PrintWriter(new FileWriter(pathFile));
    pw.println("year,core,peripheral,key,major,minor"); //year, roles, types

    for (SmallCommit c : commits) {
      calendar.setTime(c.getAuthorDate());
      int currentYear = calendar.get(Calendar.YEAR);

      if (currentYear > exportYear) {
        StringBuilder exportLine = new StringBuilder();
        exportLine.append(exportYear);
        exportLine.append(",");
        exportLine.append(numberOfCommitsPerRole.get(DeveloperRole.core) != null ? numberOfCommitsPerRole.get(DeveloperRole.core).get() : 0);
        exportLine.append(",");
        exportLine.append(numberOfCommitsPerRole.get(DeveloperRole.peripheral) != null ? numberOfCommitsPerRole.get(DeveloperRole.peripheral).get() : 0);
        exportLine.append(",");
        exportLine.append(numberOfCommitsPerType.get(DeveloperType.key) != null ? numberOfCommitsPerType.get(DeveloperType.key).get() : 0);
        exportLine.append(",");
        exportLine.append(numberOfCommitsPerType.get(DeveloperType.major) != null ? numberOfCommitsPerType.get(DeveloperType.major).get() : 0);
        exportLine.append(",");
        exportLine.append(numberOfCommitsPerType.get(DeveloperType.minor) != null ? numberOfCommitsPerType.get(DeveloperType.minor).get(): 0);
        pw.println(exportLine.toString());

        exportYear = currentYear;
        logger.info("Written number of commits per type in " + exportYear);
      }

      Identity id = PersonInformation.getInstance().getIdentityMap().get(c.getAuthorId());
      increaseNumberOfCommits(id.getRole());
      increaseNumberOfCommits(id.getType());
    }

    pw.close();
  }

  private void increaseNumberOfCommits(DeveloperRole r) {
    MutableInt p = numberOfCommitsPerRole.get(r);
    if (p == null) {
      numberOfCommitsPerRole.put(r, new MutableInt());
    } else {
      p.increment();
    }
  }

  private void increaseNumberOfCommits(DeveloperType t) {
    MutableInt p = numberOfCommitsPerType.get(t);
    if (p == null) {
      numberOfCommitsPerType.put(t, new MutableInt());
    } else {
      p.increment();
    }
  }

}
