package org.openshapa.controllers;

import org.openshapa.OpenSHAPA;
import org.openshapa.db.DataCell;
import org.openshapa.db.DataColumn;
import org.openshapa.db.Database;
import org.openshapa.db.SystemErrorException;
import org.openshapa.util.FileFilters.CSVFilter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import javax.swing.filechooser.FileFilter;
import org.apache.log4j.Logger;
import org.openshapa.db.FormalArgument;
import org.openshapa.db.MatrixVocabElement;
import org.openshapa.db.MatrixVocabElement.MatrixType;

/**
 * Controller for saving the database to disk.
 */
public final class SaveDatabaseC {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(SaveDatabaseC.class);

    /**
     * Constructor.
     *
     * @param destinationFile The destination to use when saving the CSV file.
     * @param fileFilter The selected filter to use when saving the file.
     */
    public SaveDatabaseC(final String destinationFile,
                         final FileFilter fileFilter) {
        if (fileFilter.getClass() == CSVFilter.class) {
            saveAsCSV(destinationFile + ".csv");
        }
    }

    /**
     * Saves the database to the specified destination in a CSV format.
     *
     * @param outFile The path of the file to use when writing to disk.
     */
    public void saveAsCSV(final String outFile) {
        Database db = OpenSHAPA.getDatabase();

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
            Vector<Long> colIds = db.getColOrderVector();

            //Vector<DataColumn> cols = db.getDataColumns();
            for (int i = 0; i < colIds.size(); i++) {
                DataColumn dc = db.getDataColumn(colIds.get(i));
                boolean isMatrix = false;

                out.write(dc.getName() + " (" + dc.getItsMveType() + ")");

                // If we a matrix type - we need to dump the formal args.
                MatrixVocabElement mve = db.getMatrixVE(dc.getItsMveID());
                if (dc.getItsMveType() == MatrixType.MATRIX) {
                    isMatrix = true;
                    out.write("-");
                    for (int j = 0; j < mve.getNumFormalArgs(); j++) {
                        FormalArgument fa = mve.getFormalArg(j);
                        String name = fa.getFargName()
                                   .substring(1, fa.getFargName().length() - 1);
                        out.write(name + "|" + fa.getFargType().toString());

                        if (j < mve.getNumFormalArgs() - 1) {
                            out.write(",");
                        }
                    }
                }

                out.newLine();
                for (int j = 1; j <= dc.getNumCells(); j++) {
                    DataCell c = (DataCell) dc.getDB().getCell(dc.getID(), j);
                    out.write(c.getOnset().toString());
                    out.write(",");
                    out.write(c.getOffset().toString());
                    out.write(",");
                    String value = c.getVal().toString();
                    if (!isMatrix) {
                        value = value.substring(1, value.length() - 1);
                    }
                    out.write(value);
                    out.newLine();
                }
            }
            out.close();

        } catch (IOException e) {
            logger.error("unable to save database as CSV file", e);
        } catch (SystemErrorException se) {
            logger.error("Unable to save database as CSV file", se);
        }
    }
}
