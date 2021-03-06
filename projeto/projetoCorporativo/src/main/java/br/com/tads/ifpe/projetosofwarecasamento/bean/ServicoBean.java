/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.tads.ifpe.projetosofwarecasamento.bean;

import br.com.tads.ifpe.projetosofwarecasamento.model.Casamento;
import br.com.tads.ifpe.projetosofwarecasamento.model.Profissional;
import br.com.tads.ifpe.projetosofwarecasamento.repository.ServicoRepository;
import br.com.tads.ifpe.projetosofwarecasamento.model.Servico;
import br.com.tads.ifpe.projetosofwarecasamento.model.StatusTarefa;
import br.com.tads.ifpe.projetosofwarecasamento.model.Tarefa;
import br.com.tads.ifpe.projetosofwarecasamento.repository.CasamentoRepository;
import br.com.tads.ifpe.projetosofwarecasamento.repository.TarefaRepository;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import org.omnifaces.util.Messages;

/**
 *
 * @author aluno
 */
@ManagedBean(name = "servicoBean")
@SessionScoped
public class ServicoBean implements Serializable{
    
    private Casamento casamento;
    private Servico servico;
    private List<Servico> listaServico;
    
    private Profissional profissional = null;
    
    @EJB
    private ServicoRepository servicoRepository;
    
    @EJB
    private CasamentoRepository casamentoRepository;
    
    @EJB
    private TarefaRepository tarefaRepository;
    
    @PostConstruct
    public void constroi(){
        servico = new Servico();
        
        if(FacesContext.getCurrentInstance().getExternalContext().isUserInRole("profissional")){
            inicializaProfissional();
        } else{
            inicializaCasamento();
        }
        
        listaServico = new ArrayList<>();
        
        listar();
    }
    
    private void inicializaProfissional(){
        if(profissional == null){
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            profissional = (Profissional) session.getAttribute("profissional");
        }
    }
    
    private void inicializaCasamento(){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        Integer idCasamento = (Integer) session.getAttribute("idCasamento");
        
        casamento = casamentoRepository.buscar(idCasamento);
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public List<Servico> getListaServico() {
        return listaServico;
    }

    public void setListaServico(List<Servico> listaServico) {
        this.listaServico = listaServico;
    }
    
    public void inserir(){
        
        try{
            
            servico.setProfissional(profissional);
            servico.setStatusDisponibilizado(Boolean.TRUE);
            
            servicoRepository.inserir(servico);
            
            Messages.addGlobalInfo("cadastrado com sucesso!");
            
            listar();
        }catch(Exception ex){
            
            Messages.addGlobalError("Ocorreu algum erro.");
            ex.printStackTrace();
        }
    }
    
    public void atualizar(ActionEvent evento){
            
        servico = (Servico) evento.getComponent().getAttributes().get("servicoSelecionado"); // change
    }
    
    public void deletar(ActionEvent evento){
        
        servico = (Servico) evento.getComponent().getAttributes().get("servicoSelecionado"); // change
        
        try{
            
            servicoRepository.deletar(servico);
            
            Messages.addGlobalInfo("Deletado com sucesso!");
            
            constroi();
        }catch(Exception ex){
            
            Messages.addGlobalError("Ocorreu algum erro.");
            ex.printStackTrace();
        }
    }
    
    public void buscar(){
        
        try{
            
            servico = servicoRepository.buscar(Integer.MIN_VALUE);
        }catch(Exception ex){
            
            Messages.addGlobalError("Ocorreu algum erro.");
            ex.printStackTrace();
        }
    }
    
    public void listar(){
        
        try{
            
            if(profissional != null){
                listaServico = servicoRepository.listaServicosPorProfissional(profissional);
            } else{
                listaServico = servicoRepository.listar();
            }
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void contratar(ActionEvent evento){
        servico = (Servico) evento.getComponent().getAttributes().get("servicoSelecionado");
        
        Tarefa novaTarefa = new Tarefa();
        
        novaTarefa.setTitulo(servico.getTitulo());
        novaTarefa.setDescricao(servico.getDescricao());
        novaTarefa.setStatus(StatusTarefa.PENDENTE);
        novaTarefa.setCasamento(casamento);
        novaTarefa.setServico(servico);
        
        try{
            tarefaRepository.inserir(novaTarefa);
            
            Messages.addGlobalInfo("O serviço foi contratado com sucesso.");
        } catch(Exception ex){
            Messages.addGlobalError("Ocorreu um erro inesperado.");
        }
    }
}
