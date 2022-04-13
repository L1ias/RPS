import com.jakewharton.fliptables.FlipTable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;


public class Main {
    private final User user;
    private final Computer computer;

    private enum Move {
        ROCK, PAPER, SCISSORS, LIZARD, SPOCK;

        public int compareMoves(Move otherMove) {
            if (this == otherMove)
                return 0;

            switch (this) {
                case ROCK -> {
                    if (otherMove == SCISSORS) return 1;
                    else if (otherMove == LIZARD) {
                        return 1;
                    }
                    return -1;
                }
                case PAPER -> {
                    if (otherMove == ROCK) return 1;
                    else if (otherMove == SPOCK) {
                        return 1;
                    }
                    return -1;
                }
                case SCISSORS -> {
                    if (otherMove == PAPER) return 1;
                    else if (otherMove == LIZARD) {
                        return 1;
                    }
                    return -1;
                }
                case LIZARD -> {
                    if (otherMove == PAPER) return 1;
                    else if (otherMove == SPOCK) {
                        return 1;
                    }
                    return -1;
                }
                case SPOCK -> {
                    if (otherMove == SCISSORS) return 1;
                    else if (otherMove == ROCK) {
                        return 1;
                    }
                    return -1;
                }
            }
            return 0;
        }
    }

    private static class User {
        private final Scanner inputScanner;

        public User() {
            inputScanner = new Scanner(System.in);
        }

        public Move getMove() {
            System.out.print("Available moves:\n 1 - rock\n 2 - paper\n 3 - scissors\n 4 - lizard\n 5 - Spock\n 0 - exit\n ? - help\n Enter your move:");

            String userInput = inputScanner.nextLine();
            userInput = userInput.toUpperCase();
            char firstLetter = userInput.charAt(0);
            if (firstLetter == '1' || firstLetter == '2' || firstLetter == '3' || firstLetter == '4' || firstLetter == '5' || firstLetter == '0' || firstLetter == '?') {
                switch (firstLetter) {
                    case '1':
                        return Move.ROCK;
                    case '2':
                        return Move.PAPER;
                    case '3':
                        return Move.SCISSORS;
                    case '4':
                        return Move.LIZARD;
                    case '5':
                        return Move.SPOCK;
                    case '0':
                        System.exit(0);
                    case '?':
                        String[] headers = {"User", "Rock", "Paper", "Scissors", "Lizard", "Spock"};
                        String[][] data = {
                                {"Rock", "DRAW", "WIN", "LOSE", "LOSE", "WIN"},
                                {"Paper", "LOSE", "DRAW", "WIN", "WIN", "LOSE"},
                                {"Scissors", "WIN", "LOSE", "DRAW", "LOSE", "WIN"},
                                {"Lizard", "WIN", "LOSE", "WIN", "DRAW", "LOSE"},
                                {"Spock", "LOSE", "WIN", "LOSE", "WIN", "DRAW"},
                        };
                        System.out.println(FlipTable.of(headers, data));
                }
            }

            return getMove();
        }

    }

    private static class Computer {
        public Move getMove() {
            Move[] moves = Move.values();
            Random random = new Random();
            int index = random.nextInt(moves.length);
            return moves[index];
        }
    }

    public Main() {
        user = new User();
        computer = new Computer();
    }
    public class KeyGen {

        public String generate() throws NoSuchAlgorithmException {

            SecureRandom random = SecureRandom.getInstanceStrong();
            byte[] values = new byte[32]; // 256 bit
            random.nextBytes(values);

            StringBuilder sb = new StringBuilder();
            for (byte b : values) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        }
    }

    public void startGame() throws NoSuchAlgorithmException {
        KeyGen i = new KeyGen();
        System.out.println("HMAC:" + i.generate());

        Move userMove = user.getMove();
        Move computerMove = computer.getMove();
        System.out.println("\nYour move: " + userMove);
        System.out.println("Computer move: " + computerMove + "\n");

        String text = computerMove + i.generate();
        MessageDigest crypt = MessageDigest.getInstance("SHA3-256");
        crypt.update(text.getBytes(StandardCharsets.UTF_8));

        byte[] bytes = crypt.digest();
        BigInteger bi = new BigInteger(1, bytes);
        String digest = String.format("%0" + (bytes.length << 1) + "x", bi);

        int compareMoves = userMove.compareMoves(computerMove);
        switch (compareMoves) {
            case 0 -> System.out.println("Tie!");
            case 1 -> System.out.println(userMove + " beats " + computerMove + ". You win!" + "\nHMAC key:" + digest);
            case -1 -> System.out.println(computerMove + " beats " + userMove + ". You lose." + "\nHMAC key:" + digest);
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Main game = new Main();
        game.startGame();
    }
}