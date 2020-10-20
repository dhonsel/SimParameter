package de.ugoe.cs.tcs.simparameter.util;

import ch.qos.logback.classic.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class CommonGit {
  private static Logger logger = (Logger) LoggerFactory.getLogger("de.ugoe.cs.tcs.simparameter.util.CommonGit");

  public static void resetHard(String rev) {
    FileRepositoryBuilder builder = new FileRepositoryBuilder();
    try {
      Repository repository = builder.setGitDir(new File(Parameter.getInstance().getRepoPath() + "/.git"))
          .readEnvironment() // scan environment GIT_* variables
          .findGitDir()      // scan up the file system tree
          .build();

      Git git = new Git(repository);
      git.reset().setMode(ResetCommand.ResetType.HARD).setRef(rev).call();
      logger.info("Repository set to rev: " + rev);
    } catch (IOException | GitAPIException e) {
      logger.error(e.getMessage());
    }
  }

}
