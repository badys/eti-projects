using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class OptionsController : MonoBehaviour {

    [SerializeField]
    private Text difficultyLabel;

    public float difficulty = 0f;

    void Start()
    {
        adjustMazeSize(difficulty);
    }

    public void adjustMazeSize(float newSize)
    {
        difficulty = newSize;
        difficultyLabel.text = difficulty == 0f ? "Amateur" : "Veteran";
        PlayerPrefs.SetInt("difficulty", (int)difficulty);
    }
}
