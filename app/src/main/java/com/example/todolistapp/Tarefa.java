package com.example.todolistapp;

public class Tarefa {
    private long   id;
    private String descricao;
    private String dataAlvo;
    private String dataRegistro;
    private boolean concluida;

    public Tarefa(long id, String descricao, String dataAlvo, String dataRegistro, boolean concluida) {
        this.id           = id;
        this.descricao    = descricao;
        this.dataAlvo     = dataAlvo;
        this.dataRegistro = dataRegistro;
        this.concluida    = concluida;
    }

    // Convenience constructor (nova tarefa ainda não persistida)
    public Tarefa(String descricao, String dataAlvo) {
        this(-1, descricao, dataAlvo, "", false);
    }

    public long    getId()          { return id; }
    public void    setId(long id)   { this.id = id; }
    public String  getDescricao()   { return descricao; }
    public String  getDataAlvo()    { return dataAlvo; }
    public String  getDataRegistro(){ return dataRegistro; }
    public boolean isConcluida()    { return concluida; }
    public void    setConcluida(boolean concluida) { this.concluida = concluida; }
}