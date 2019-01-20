using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using HTC.UnityPlugin.Vive;

[RequireComponent(typeof(CharacterController))]

public class CharacterMovement : MonoBehaviour
{
    public float speed = 6.0f;
    public float gravity = -9.8f;
    public Transform vrCameraParent;

    private CharacterController charController;
    private Transform vrCamera;
    private float x, z;

    void Start()
    {
        charController = GetComponent<CharacterController>();
        vrCamera = Camera.main.transform;
    }

    void Update()
    {
        MoveCharacter();
        MoveCamera();
        RotateCharacter();
    }

    private void MoveCharacter()
    {
        Vector2 axisMovement = ViveInput.GetPadPressAxis(HandRole.LeftHand);
        
        if (axisMovement != Vector2.zero)
        {
            float deltaX = axisMovement.x * speed;
            float deltaZ = axisMovement.y * speed;

            Vector3 movement = new Vector3(deltaX, 0, deltaZ);
            movement = Vector3.ClampMagnitude(movement, speed);

            movement.y = gravity;
            movement *= Time.deltaTime;
            movement = transform.TransformDirection(movement);   

            charController.Move(movement);
        }
    }
    
    private void RotateCharacter()
    {
        gameObject.transform.rotation = Quaternion.Euler(new Vector3(0, vrCamera.transform.rotation.eulerAngles.y, 0));
    }

    private void MoveCamera()
    {
        x = 0.05f * Mathf.Sin(vrCamera.rotation.eulerAngles.y * Mathf.Deg2Rad);
        z = 0.05f * Mathf.Cos(vrCamera.rotation.eulerAngles.y * Mathf.Deg2Rad);
        vrCameraParent.position = transform.position + new Vector3(x, 0, z);
    }
}
