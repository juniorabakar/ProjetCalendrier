import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        String inputFilename = "C:\\Users\\Junio\\Downloads\\ProjetCal\\src\\example.ics"; // Le nom de votre fichier iCalendar
        String outputFilename = "C:\\Users\\Junio\\Downloads\\ProjetCal\\src\\events_output.txt"; // Le nom du fichier de sortie

        Map<String, Evenement> evenements = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilename))) {
            String line;
            boolean inEvent = false;
            String currentUid = null;
            Evenement currentEvent = null;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("BEGIN:VEVENT")) {
                    inEvent = true;
                    currentEvent = new Evenement();
                } else if (line.startsWith("END:VEVENT")) {
                    inEvent = false;
                    if (currentEvent != null && currentUid != null) {
                        evenements.put(currentUid, currentEvent);
                    }
                } else if (inEvent && (line.contains(":") || line.contains(";"))) {
                    int indexOfColon = line.indexOf(':');
                    int indexOfSemicolon = line.indexOf(';');
                    if (indexOfColon == -1 && indexOfSemicolon == -1) {
                        continue; // Ignore la ligne si elle ne contient pas de ':' ou de ';'
                    }
                    int delimiterIndex = Math.min(indexOfColon != -1 ? indexOfColon : Integer.MAX_VALUE,
                            indexOfSemicolon != -1 ? indexOfSemicolon : Integer.MAX_VALUE);

                    String key = line.substring(0, delimiterIndex);
                    String value = line.substring(delimiterIndex + 1).trim(); // Supprime les espaces autour de la valeur

                    // Définissez les attributs de l'événement en fonction de la clé
                    switch (key) {
                        case "SUMMARY":
                            currentEvent.setSummary(value);
                            break;
                        case "LOCATION":
                            currentEvent.setLocation(value);
                            break;
                        case "DESCRIPTION":
                            currentEvent.setDescription(value);
                            break;
                        case "CATEGORIES":
                            currentEvent.setCategories(value);
                            break;
                        case "UID":
                            currentUid = value;
                            currentEvent.setUid(value);
                            break;
                        case "DTSTART":
                            currentEvent.setDtStart(parseDateTime(value));
                            break;
                        case "DTEND":
                            currentEvent.setDtEnd(parseDateTime(value));
                            break;
                        case "DTSTAMP":
                            currentEvent.setDtStamp(parseDateTime(value));
                            break;
                        case "LAST-MODIFIED":
                            currentEvent.setLastModified(parseDateTime(value));
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Écrivez chaque événement dans le fichier de sortie
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilename))) {
            for (Evenement event : evenements.values()) {
                bw.write(event.toString());
                bw.newLine(); // Ajoute une nouvelle ligne
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode utilitaire pour analyser une chaîne de date/heure en un objet LocalDateTime
  private static LocalDateTime parseDateTime(String dateTimeStr) {
    if (dateTimeStr.startsWith("VALUE=DATE:")) {
        // Si le format est "VALUE=DATE:YYYYMMDD", extrayez la partie YYYYMMDD et parsez-la comme une LocalDate
        String dateStr = dateTimeStr.substring("VALUE=DATE:".length());
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.BASIC_ISO_DATE);
        // Convertissez LocalDate en LocalDateTime en utilisant minuit comme heure
        return date.atStartOfDay();
    } else {
        // Parsez la chaîne de date/heure comme d'habitude
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
    }
}

}
