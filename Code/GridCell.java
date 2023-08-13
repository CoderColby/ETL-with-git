import java.util.ArrayList;

public class GridCell {

  public static final byte RIGHT_WALL = 0;
  public static final byte BOTTOM_WALL = 1;
  public static final byte LEFT_WALL = 2;
  public static final byte TOP_WALL = 4;

  private RoomType roomType;
  private Item item;
  private Entity entity;
  private Wall[] walls;
  private int[] rowColumn;
  private GameBoard board;

  public GridCell(String[] fileContents, int coordinates, GameBoard board) {
    rowColumn = int[2] {coordinates / 10, coordinates % 10};
    this.board = board;
    walls = new Wall[2];
    walls[0] = Wall.getWallByTag(fileContents[0]);
    walls[1] = Wall.getWallByTag(fileContents[1]);
    roomType = RoomType.getRoomTypeByTag(fileContents[2]);
    item = Item.getItemByTag(fileContents[3]);
    entity = Entity.getEntityByTag(fileContents[4]);
  }

  public char getRoomType() {
    return interior;
  }

  public char getItem() {
    return item;
  }

  public Entity getEntity() {
    return occupant;
  }

  public Wall getWall(byte direction) {
    if (direction < 2)
      return walls[direction];
    else if ((direction == 2 && rowColumn[1] == 0) || (direction == 3 && row_column[0] == 0))
      return board.getCell(row_column[0], row_column[1], direction).getWall(direction % 2);
    return new RegularWall();
  }

  
}