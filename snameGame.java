import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashSet;

int speed = 200;        // Adjust this value to change the snake's speed.
int cubeSize = 10;     // Size of each grid square.
int sizeOfSnake = 4;   // Initial snake length.
int x, y;              // Snake head coordinates.
ArrayList<Position> snaky = new ArrayList<Position>();  // Snake's body.
Position food;

class Position {
  int x, y;
  Position(int x, int y) {
    this.x = x - (x % cubeSize);
    this.y = y - (y % cubeSize);
  }
  
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Position)) return false;
    Position other = (Position) obj;
    return this.x == other.x && this.y == other.y;
  }
  
  public int hashCode() {
    return x * 31 + y;
  }
}

void setup() {
  //size(420, 700); //mobile
  size(700,700);
  frameRate(speed);
  initializeGame();
}

void initializeGame() {
  sizeOfSnake = 4;
  snaky.clear();
  
  int startX = (width / 2 / cubeSize) * cubeSize;
  int startY = (height / 2 / cubeSize) * cubeSize;
  x = startX;
  y = startY;
  
  snaky.add(new Position(startX, startY));
  snaky.add(new Position(startX, startY - cubeSize));
  snaky.add(new Position(startX, startY - 2 * cubeSize));
  snaky.add(new Position(startX, startY - 3 * cubeSize));
  
  food = generateFood();
  frameRate(speed);  // Set speed dynamically.
  loop();
}

Position generateFood() {
  Position newFood;
  do {
    newFood = new Position((int) random(0, width), (int) random(0, height));
  } while (snaky.contains(newFood));
  return newFood;
}

void cubes() {
  for (int i = 0; i < width; i += cubeSize) {
    for (int j = 0; j < height; j += cubeSize) {
      noFill();
      stroke(30);
      strokeWeight(1);
      rect(i, j, cubeSize, cubeSize);
    }
  }
}

void draw() {
  background(50);
  cubes();
  snake();
}

void snake() {
  fill(200, 0, 40);
  rect(food.x, food.y, cubeSize, cubeSize);
  
  Position nextMove = chooseBestMove();
  if (nextMove == null) {
    gameOver();
    return;
  }
  
  x = nextMove.x;
  y = nextMove.y;
  Position newHead = new Position(x, y);
  
  if (snaky.contains(newHead)) {
    gameOver();
    return;
  }
  
  snaky.add(0, newHead);
  if (newHead.equals(food)) {
    sizeOfSnake++;  
    food = generateFood();
  } else {
    while (snaky.size() > sizeOfSnake) {
      snaky.remove(snaky.size() - 1);
    }
  }
  
  fill(0, 200, 40);
  for (Position p : snaky) {
    rect(p.x, p.y, cubeSize, cubeSize);
  }
}

Position chooseBestMove() {
  Position head = new Position(x, y);
  Position bestMove = null;
  int bestScore = -1000000;
  
  int[] dx = {0, 0, cubeSize, -cubeSize};
  int[] dy = {-cubeSize, cubeSize, 0, 0};
  
  for (int i = 0; i < 4; i++) {
    Position next = new Position(head.x + dx[i], head.y + dy[i]);
    if (!isValidMove(next)) continue;
    
    boolean eats = next.equals(food);
    ArrayList<Position> newSnake = simulateSnake(next, eats);
    int openArea = floodFillCount(next, newSnake);
    
    if (openArea < newSnake.size()) continue;
    
    int dist = manhattanDistance(next, food);
    int score = (eats ? 10000 : (-dist)) + openArea;
    
    if (score > bestScore) {
      bestScore = score;
      bestMove = next;
    }
  }
  
  return bestMove;
}

ArrayList<Position> simulateSnake(Position newHead, boolean eats) {
  ArrayList<Position> newSnake = new ArrayList<Position>(snaky);
  newSnake.add(0, newHead);
  if (!eats) {
    newSnake.remove(newSnake.size() - 1);
  }
  return newSnake;
}

int manhattanDistance(Position a, Position b) {
  return abs(a.x - b.x) + abs(a.y - b.y);
}

boolean isValidMove(Position pos) {
  if (pos.x < 0 || pos.x >= width || pos.y < 0 || pos.y >= height) return false;
  if (snaky.contains(pos)) return false;
  return true;
}

int floodFillCount(Position start, ArrayList<Position> obstacles) {
  HashSet<Position> obs = new HashSet<Position>(obstacles);
  Queue<Position> queue = new LinkedList<Position>();
  HashSet<Position> visited = new HashSet<Position>();
  
  queue.add(start);
  visited.add(start);
  
  int count = 0;
  int[] dx = {0, 0, cubeSize, -cubeSize};
  int[] dy = {cubeSize, -cubeSize, 0, 0};
  
  while (!queue.isEmpty()) {
    Position cur = queue.poll();
    count++;
    for (int i = 0; i < 4; i++) {
      Position nxt = new Position(cur.x + dx[i], cur.y + dy[i]);
      if (nxt.x < 0 || nxt.x >= width || nxt.y < 0 || nxt.y >= height) continue;
      if (!visited.contains(nxt) && !obs.contains(nxt)) {
        visited.add(nxt);
        queue.add(nxt);
      }
    }
  }
  
  return count;
}

void keyPressed() {
  if (key == 'r' || key == 'R') {
    initializeGame();
  } else if (key == 'f' || key == 'F') {
    speed += 2; // Increase speed.
    frameRate(speed);
  } else if (key == 's' || key == 'S') {
    speed = max(2, speed - 2); // Decrease speed, but not below 2.
    frameRate(speed);
  }
}

void gameOver() {
  fill(200, 200, 230, 200);
  rect(80, 250, 520, 130, 50);
  textSize(50);
  fill(100, 20, 20);
  strokeWeight(3);
  text("GAME OVER", 100, 350);
  noLoop();
}
