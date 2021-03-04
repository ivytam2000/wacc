package backend;

public class trial {

  public static void main(String[] args) {
    String line = "0x12312312 Hello there";
    String line2 = "0x12412412 Hello there";
    line = line.replaceAll("0x[0-9]+", "0xaaaaaaaa");
    line2 = line2.replaceAll("0x[0-9]+", "0xaaaaaaaa");

    System.out.println(line);
    System.out.println(line2);
    System.out.println(line.hashCode() == line2.hashCode());
  }

}
