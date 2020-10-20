package de.ugoe.cs.tcs.simparameter.util;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import org.apache.commons.cli.*;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * This class provides parameter to the entire application. It reads the command line
 * arguments and assigns the options to appropriate parameter. To avoid multiple instances
 * the singleton pattern is used.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class Parameter {
  Logger logger = (Logger) LoggerFactory.getLogger("de.ugoe.cs.tcs.simparameter..util.Parameter");
  private static Parameter instance;
  final private String version = "0.10";

  // required parameter
  private String configFile;
  private String outputFolder;

  // boolean parameter
  private boolean showVersion;
  private boolean showHelp;
  private boolean ssl;

  // database parameter
  private String dbName;
  private String dbUser;
  private String dbPassword;
  private String dbHostname;
  private int dbPort;
  private String dbAuthentication;

  // parameters written in configuration file
  private List<String> ccExportCommits;
  private String url;
  private String repoPath;
  private String dirToAnalyze;
  private List<String> sgCommits;
  private boolean determinedGrowthPhase;
  private int sizeGrowthPhase; // Number of commits
  private int maxPackageSplitName;
  private double maxFilesToAnalyze; // Percent of files to analyze
  private int minPackageSize; // min class count per package
  private List<String> refactoringTypes;

  // debug parameter
  private String debugLevel;

  private boolean initialized = false;
  private final OptionHandler optionsHandler;

  private Parameter() {
    optionsHandler = new OptionHandler();
    ccExportCommits = Lists.newArrayList();
    sgCommits = Lists.newArrayList();
    refactoringTypes = Lists.newArrayList();
  }

  public static synchronized Parameter getInstance() {
    if (instance == null) {
      instance = new Parameter();
    }
    return instance;
  }

  public void init(String[] args) {
    CommandLine cmd = parseCommandLineArguments(args);

    configFile = "config/" + cmd.getOptionValue("c");
    outputFolder = cmd.getOptionValue("o");
    showVersion = cmd.hasOption("v");
    showHelp = cmd.hasOption("h");
    ssl = cmd.hasOption("ssl");
    dbName = cmd.getOptionValue("DB", "smartshark");
    dbUser = cmd.getOptionValue("U", "");
    dbPassword = cmd.getOptionValue("P", "");
    dbHostname = cmd.getOptionValue("H", "localhost");
    dbPort = Integer.parseInt(cmd.getOptionValue("p", "27017"));
    dbAuthentication = cmd.getOptionValue("a", "");
    debugLevel = cmd.getOptionValue("d", "ERROR");

    setConfigFileParams();
    initialized = true;
  }

  private void setConfigFileParams() {
    Configurations configs = new Configurations();
    try
    {
      Configuration config = configs.properties(new File(configFile));
      if (config.getList(String.class, "cc.commits") != null) {
        ccExportCommits.addAll(config.getList(String.class, "cc.commits"));
      }
      url = config.getString("config.url");
      repoPath = config.getString("config.repoPath");
      dirToAnalyze = config.getString("config.dirToAnalyze");
      if (config.getList(String.class, "sg.commits") != null) {
        sgCommits.addAll(config.getList(String.class, "sg.commits"));
      }
      sizeGrowthPhase = config.getInt("config.sizeGrowthPhase");
      determinedGrowthPhase = config.getBoolean("config.determinedGrowthPhase");
      maxPackageSplitName = config.getInt("config.maxPackageSplitName");
      maxFilesToAnalyze = config.getDouble("config.maxFilesToAnalyze");
      minPackageSize = config.getInt("config.minPackageSize");
      if (config.getList(String.class, "config.refTypes") != null) {
        refactoringTypes.addAll(config.getList(String.class, "config.refTypes"));
      }
    }
    catch (ConfigurationException e)
    {
      logger.error(e.getMessage());
    }
  }

  public String getConfigFile() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return configFile;
  }

  public List<String> getCcExportCommits() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return ccExportCommits;
  }

  public String getOutputFolder() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return outputFolder;
  }

  public String getUrl() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return url;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public String getDbName() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return dbName;
  }

  public boolean isShowVersion() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return showVersion;
  }

  public boolean isShowHelp() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return showHelp;
  }

  public boolean isSsl() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return ssl;
  }

  public String getDbUser() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return dbUser;
  }

  public String getDbPassword() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return dbPassword;
  }

  public String getDbHostname() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return dbHostname;
  }

  public int getDbPort() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return dbPort;
  }

  public String getDbAuthentication() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return dbAuthentication;
  }

  public String getDebugLevel() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return debugLevel;
  }

  public String getDirToAnalyze() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return dirToAnalyze;
  }

  public List<String> getSgCommits() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return sgCommits;
  }

  public List<String> getRefactoringTypes() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return refactoringTypes;
  }

  public int getSizeGrowthPhase() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return sizeGrowthPhase;
  }

  public boolean isDeterminedGrowthPhase() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return determinedGrowthPhase;
  }

  public String getRepoPath() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return repoPath;
  }

  public int getMaxPackageSplitName() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return maxPackageSplitName;
  }

  public double getMaxFilesToAnalyze() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return maxFilesToAnalyze;
  }

  public int getMinPackageSize() {
    if (!isInitialized()) {
      logger.warn("The current parameter instance is not initialized!");
    }
    return minPackageSize;
  }

  private CommandLine parseCommandLineArguments(String[] args) {
    CommandLineParser parser =  new DefaultParser();
    CommandLine commandLine = null;
    try {
      commandLine = parser.parse(optionsHandler.getOptions(), args);
      if (commandLine.hasOption("h") ) {
        printHelp();
        System.exit(0);
      } else if (commandLine.hasOption("v")) {
        printVersion();
        System.exit(0);
      } else if (!commandLine.hasOption("c")) {
        logger.error("Missing required option: c");
        printHelp();
        System.exit(1);
      } else if (!commandLine.hasOption("o")) {
        logger.error("Missing required option: o");
        printHelp();
        System.exit(1);
      }
    } catch (ParseException e) {
      logger.error(e.getMessage());
      printHelp();
      System.exit(1);
    }
    return commandLine;
  }

  public void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "simSHARK", optionsHandler.getOptions() );
  }

  public void printVersion() {
    System.out.println("This is simSHARK version " + version);
  }

}
