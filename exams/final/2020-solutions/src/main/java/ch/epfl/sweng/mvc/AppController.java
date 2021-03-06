package ch.epfl.sweng.mvc;

import ch.epfl.sweng.Student;
import ch.epfl.sweng.database.DatabaseException;

/**
 * This is the controller of the MVC application. It handles incoming user commands, uses
 * the model to get the necessary data and uses the view to construct a response to the user.
 *
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * You CAN change the bodies of existing methods/constructors
 * You CAN add new private methods/constructors
 * You CANNOT add interface implementations unless explicitly instructed to do so
 * You CANNOT add new public, package-private or protected methods/constructors
 * You CANNOT edit the names, parameters, checked exceptions or return types of existing methods/constructors
 * You CANNOT delete existing methods/constructors
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
public final class AppController {

    private final AppModel model;
    private final AppView view;

    /**
     * Constructs a new controller.
     * It requires references to both the model and the view to function.
     *
     * @param model The MVC model that contains the student data.
     * @param view  The MVC view that crafts user responses.
     * @throws IllegalArgumentException Thrown if either argument is null.
     */
    public AppController(AppModel model, AppView view) {
        if (model == null || view == null) {
            throw new IllegalArgumentException();
        }

        this.model = model;
        this.view = view;
    }

    /**
     * Handles an incoming user request.
     * Here is the list of supported commands and their behaviour:
     *
     * - put [name] [sciper] [faculty]
     * Example: put John 123456 IC
     *
     * Inserts a new student in the database. The student should have the
     * name, SCIPER and faculty provided by the arguments. If the number of
     * argument for this command does not match, the view should respond with
     * an error message containing "Invalid number of arguments in 'put' command".
     * If the operation succeeds, the view should respond with a success message
     * containing "Successfully added student '{sciper}'", where {sciper} is replaced
     * by the student's SCIPER. If an exception occurs in the database, the view
     * should respond with an error message containing "Database failed to insert student".
     *
     * - remove [sciper]
     * Example: remove 123456
     *
     * Removes a student from the database. The student that is removed should have
     * the same SCIPER value as the one provided in the argument. If the number of
     * argument for this command does not match, the view should respond with an error
     * message containing "Invalid number of arguments in 'remove' command".
     * If the operation succeeds, the view should respond with a success message
     * containing "Successfully removed student '{sciper}'", where {sciper} is replaced
     * by the student's SCIPER. If an exception occurs in the database, the view should
     * respond with an error message containing "Database failed to remove student".
     *
     * - get [sciper]
     * Example: get 123456
     *
     * Queries a student from the database and displays it to the user. The search should be
     * based on the SCIPER value provided as argument to the command. If the number of argument
     * for this command does not match, the view should respond with an error message
     * containing "Invalid number of arguments in 'get' command". If the operation succeeds,
     * the view should respond with the formatted result of the query. If an exception occurs
     * in the database, the view should respond with an error message
     * containing "Database failed to retrieve student".
     *
     * Notes:
     * - If the request does not match with any of the commands above, the view should respond with
     * an error message containing "Invalid command".
     * - This method should ignore any leading/trailing whitespaces in the input string.
     * - Multiple spaces between command arguments in the input string can be considered invalid.
     * - A number of command arguments that is greater than expected should be considered invalid.
     * - It is not required for the command input parameters to be validated.
     *
     * @param request The user request to process.
     * @return The result of this request as per formatted by the view.
     * @throws IllegalArgumentException Thrown if the argument is null.
     */
    public String handleRequest(String request) {
        if (request == null) {
            throw new IllegalArgumentException();
        }
        String[] args = request.trim().split(" ");
        if (args.length < 1) {
            return view.getErrorMsg("Invalid command");
        }
        switch (args[0]) {
            case "put": {
                if (args.length != 4) {
                    return view.getErrorMsg("Invalid number of arguments in 'put' command");
                }
                Student student = new Student(args[1], args[2], args[3]);
                try {
                    model.put(student);
                } catch (DatabaseException de) {
                    return view.getErrorMsg("Database failed to insert student");
                }
                return view.getSuccessMsg("Successfully added student '" + args[2] + "'");
            }
            case "remove": {
                if (args.length != 2) {
                    return view.getErrorMsg("Invalid number of arguments in 'remove' command");
                }
                try {
                    model.remove(args[1]);
                } catch (DatabaseException e) {
                    return view.getErrorMsg("Database failed to remove student");
                }
                return view.getSuccessMsg("Successfully removed student '" + args[1] + "'");
            }
            case "get": {
                if (args.length != 2) {
                    return view.getErrorMsg("Invalid number of arguments in 'get' command");
                }
                try {
                    ModelGetResult result = model.get(args[1]);
                    return view.getStudentResultMsg(args[1], result.duration, result.student);
                } catch (DatabaseException e) {
                    return view.getErrorMsg("Database failed to retrieve student");
                }
            }
            default: {
                return view.getErrorMsg("Invalid command");
            }
        }
    }
}
