using System;
using UnityEngine;
using UnityEngine.UI;

[RequireComponent(typeof(MazeConstructor))]

public class GameController : MonoBehaviour
{

    [SerializeField]
    private CharacterMovement player;
    [SerializeField]
    private Text timeLabel;

    public int timeLimit = 300;

    private MazeConstructor generator;
    private DateTime startTime;

    private int mazeSize = 8;
    private int difficulty;
    private int score = 0;

    void Start()
    {
        generator = GetComponent<MazeConstructor>();
        difficulty = PlayerPrefs.GetInt("difficulty");
        mazeSize *= difficulty == 0 ? 1 : 2;
        StartNewGame();
    }

    public void StartNewGame()
    {
        startTime = DateTime.Now;
        StartNewMaze();
    }

    private void StartNewMaze()
    {
        mazeSize += difficulty == 0 ? 5 : 10;
        generator.GenerateNewMaze(mazeSize, OnGoalTrigger, OnKeyTrigger);

        float x = generator.startCol * generator.hallWidth;
        float y = 1;
        float z = generator.startRow * generator.hallWidth;
        player.transform.position = new Vector3(x, y, z);

        player.enabled = true;
    }

    void Update()
    {
        if (!player.enabled)
        {
            return;
        }

        int timeUsed = (int)(DateTime.Now - startTime).TotalSeconds;
        int timeLeft = timeLimit - timeUsed;

        if (timeLeft > 0)
        {
            timeLabel.text = timeLeft.ToString();
        }
        else
        {
            timeLabel.text = "GAME OVER";
            player.enabled = false;
            Invoke("returnToMenu", 4);
        }
    }

    private void OnGoalTrigger(GameObject trigger, GameObject other)
    {
        Debug.Log("Goal!");
        player.enabled = false;
        startTime = startTime.AddSeconds(mazeSize);
        score += mazeSize;
        Invoke("StartNewMaze", 2);
    }

    private void OnKeyTrigger(GameObject trigger, GameObject other)
    {
        if(other.name.Equals("Key"))
        {
            Debug.Log("Door unlocked");
            trigger.gameObject.SetActive(false);
            other.gameObject.SetActive(false);
        }
    }

    private void returnToMenu()
    {
        PlayerPrefs.SetInt("lastScore", score);
        UnityEngine.SceneManagement.SceneManager.LoadScene(0);
    }
}