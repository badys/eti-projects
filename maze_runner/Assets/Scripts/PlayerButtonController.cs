using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using HTC.UnityPlugin.Vive;

public class PlayerButtonController : MonoBehaviour {

    private HandRole leftHand = HandRole.LeftHand;
    private HandRole rightHand = HandRole.RightHand;
    private ControllerButton triggerButton = ControllerButton.Trigger;

    public Canvas timerCanvas;

	// Use this for initialization
    void Start()
    {
        		
	}
	
	// Update is called once per frame
	void Update () {
        timerCanvas.gameObject.SetActive(ViveInput.GetPress(leftHand, triggerButton));
	}
}
