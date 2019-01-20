using System;
using UnityEngine;

public class MazeConstructor : MonoBehaviour {

    [SerializeField]
    private Material mazeMat1;
    [SerializeField]
    private Material mazeMat2;
    [SerializeField]
    private GameObject goal;
    [SerializeField]
    private GameObject blockade;
    [SerializeField]
    private GameObject keyObject;

    private MazeDataGenerator dataGenerator;
    private MazeMeshGenerator meshGenerator;

    public int[,] data {get; private set;}

    public float hallWidth { get; private set; }
    public float hallHeight { get; private set; }

    public int startRow { get; private set; }
    public int startCol { get; private set; }

    public int keyRow { get; private set; }
    public int keyCol { get; private set; }

    public int goalRow { get; private set; }
    public int goalCol { get; private set; }

    void Awake()
    {
        // default to walls surrounding a single empty cell
        data = new int[,]
        {
            {1, 1, 1},
            {1, 0, 1},
            {1, 1, 1}
        };
        dataGenerator = new MazeDataGenerator();
        meshGenerator = new MazeMeshGenerator();
    }

    public void GenerateNewMaze(int size, TriggerEventHandler goalCallback = null, TriggerEventHandler keyCallback = null)
    {
        int sizeRows, sizeCols;
        sizeRows = sizeCols = size % 2 == 0 ? size + 1 : size;

        DisposeOldMaze();

        data = dataGenerator.FromDimensions(sizeRows, sizeCols);

        FindStartPosition();
        FindKeyPosition();
        FindGoalPosition();

        // store values used to generate this mesh
        hallWidth = meshGenerator.width;
        hallHeight = meshGenerator.height;

        DisplayMaze();

        PlaceGoalTrigger(goalCallback, keyCallback);
    }

    private void DisplayMaze()
    {
        GameObject go = new GameObject();
        go.transform.position = Vector3.zero;
        go.name = "Procedural Maze";
        go.tag = "Generated";

        MeshFilter mf = go.AddComponent<MeshFilter>();
        mf.mesh = meshGenerator.FromData(data);

        MeshCollider mc = go.AddComponent<MeshCollider>();
        mc.sharedMesh = mf.mesh;

        MeshRenderer mr = go.AddComponent<MeshRenderer>();
        mr.materials = new Material[2] { mazeMat1, mazeMat2 };
    }

    public void DisposeOldMaze()
    {
        GameObject[] objects = GameObject.FindGameObjectsWithTag("Generated");
        foreach (GameObject go in objects)
        {
            Destroy(go);
        }
    }

    private void FindStartPosition()
    {
        int[,] maze = data;
        int rMax = maze.GetUpperBound(0);
        int cMax = maze.GetUpperBound(1);

        // loop top to bottom, right to left
        for (int i = 0; i <= rMax; i++)
        {
            for (int j = 0; j <= cMax; j++)
            {
                if (maze[i, j] == 0)
                {
                    startRow = i;
                    startCol = j;
                    return;
                }
            }
        }
    }

    private void FindKeyPosition()
    {
        int[,] maze = data;
        int rMax = maze.GetUpperBound(0);
        int cMax = maze.GetUpperBound(1);

        // loop top to bottom, right to left
        for (int j = cMax; j >= 0; j--)
        {
            for (int i = 0; i <= rMax; i++)
            {
                if (maze[i, j] == 0)
                {
                    keyRow = i;
                    keyCol = j;
                    return;
                }
            }
        }
    }

    private void FindGoalPosition()
    {
        int[,] maze = data;
        int rMax = maze.GetUpperBound(0);
        int cMax = maze.GetUpperBound(1);

        // loop top to bottom, right to left
        for (int i = rMax; i >= 0; i--)
        {
            for (int j = cMax; j >= 0; j--)
            {
                if (maze[i, j] == 0)
                {
                    goalRow = i;
                    goalCol = j;
                    return;
                }
            }
        }
    }

    private void PlaceGoalTrigger(TriggerEventHandler callback, TriggerEventHandler blockadeCallback)
    {
        blockade.gameObject.SetActive(true);
        keyObject.gameObject.SetActive(true);

        goal.transform.position = new Vector3(goalCol * hallWidth, 0.5f, goalRow * hallWidth);
        TriggerEventRouter tc = goal.AddComponent<TriggerEventRouter>();
        tc.callback = callback;

        blockade.transform.position = new Vector3(goalCol * hallWidth, 0, goalRow * hallWidth);
        TriggerEventRouter ter = blockade.AddComponent<TriggerEventRouter>();
        ter.callback = blockadeCallback;

        keyObject.GetComponent<Rigidbody>().position = new Vector3(keyCol * hallWidth, 1f, keyRow * hallWidth);
    }
}
