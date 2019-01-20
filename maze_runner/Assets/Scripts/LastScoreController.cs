using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class LastScoreController : MonoBehaviour
{
    [SerializeField]
    private GameObject scorePanel;
    [SerializeField]
    private Text scoreLabel;

    void Start()
    {
        int score = PlayerPrefs.GetInt("lastScore");
        if (score > 0)
        {
            scorePanel.gameObject.SetActive(true);
            scoreLabel.text = score.ToString();
        }
    }
		
}
