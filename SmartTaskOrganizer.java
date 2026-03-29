import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

enum Priority {
    HIGH, MEDIUM, LOW
}

enum Category {
    WORK, STUDY, PERSONAL, OTHER
}

class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private String description;
    private boolean isCompleted;
    private Priority priority;
    private Category category;

    public Task(String description, Priority priority, Category category) {
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.isCompleted = false;
    }

    public boolean isCompleted() { return isCompleted; }
    public void markComplete() { this.isCompleted = true; }

    @Override
    public String toString() {
        String status = isCompleted ? "[✓]" : "[ ]";
        return String.format("%s %-30s | Category: %-10s | Priority: %s", 
                status, description, category, priority);
    }
}

public class SmartTaskOrganizer {
    private static final String DATA_FILE = "task_data.ser";
    private ArrayList<Task> tasks = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        SmartTaskOrganizer app = new SmartTaskOrganizer();
        app.loadData();
        app.run();
    }

    private void run() {
        System.out.println("=== Smart Task Organizer ===");
        boolean running = true;

        while (running) {
            System.out.println("\n1. View All Tasks\n2. Add New Task\n3. Mark Task as Complete\n4. Clear Completed Tasks\n5. Save & Exit");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": displayTasks(); break;
                case "2": addTask(); break;
                case "3": completeTask(); break;
                case "4": clearCompleted(); break;
                case "5": 
                    saveData(); 
                    running = false; 
                    System.out.println("Goodbye!");
                    break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void displayTasks() {
        System.out.println("\n--- Your Task List ---");
        if (tasks.isEmpty()) {
            System.out.println("No tasks found. You're all caught up!");
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    private void addTask() {
        System.out.print("Enter task description: ");
        String desc = scanner.nextLine();

        System.out.print("Select Priority (1: HIGH, 2: MEDIUM, 3: LOW): ");
        Priority priority = Priority.MEDIUM; // Default
        try {
            int p = Integer.parseInt(scanner.nextLine());
            if (p == 1) priority = Priority.HIGH;
            else if (p == 3) priority = Priority.LOW;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, defaulting to MEDIUM.");
        }

        System.out.print("Select Category (1: WORK, 2: STUDY, 3: PERSONAL, 4: OTHER): ");
        Category category = Category.OTHER; // Default
        try {
            int c = Integer.parseInt(scanner.nextLine());
            if (c == 1) category = Category.WORK;
            else if (c == 2) category = Category.STUDY;
            else if (c == 3) category = Category.PERSONAL;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, defaulting to OTHER.");
        }

        tasks.add(new Task(desc, priority, category));
        System.out.println("Task added successfully!");
    }

    private void completeTask() {
        displayTasks();
        if (tasks.isEmpty()) return;

        System.out.print("Enter the number of the task to mark complete: ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index >= 0 && index < tasks.size()) {
                Task t = tasks.get(index);
                if (!t.isCompleted()) {
                    t.markComplete();
                    System.out.println("Task marked as complete!");
                } else {
                    System.out.println("Task is already completed.");
                }
            } else {
                System.out.println("Invalid task number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void clearCompleted() {
        int initialSize = tasks.size();
        tasks.removeIf(Task::isCompleted);
        int removed = initialSize - tasks.size();
        System.out.println("Cleared " + removed + " completed task(s).");
    }

    private void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(tasks);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                tasks = (ArrayList<Task>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading data. Starting fresh.");
            }
        }
    }
}
