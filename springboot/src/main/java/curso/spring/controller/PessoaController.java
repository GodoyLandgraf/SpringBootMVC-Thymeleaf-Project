package curso.spring.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import curso.spring.model.Pessoa;
import curso.spring.model.Telefone;
import curso.spring.repository.PessoaRepository;
import curso.spring.repository.TelefoneRepository;

@Controller
public class PessoaController {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;

	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoaobj", new Pessoa());
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIt);
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")
	public ModelAndView salvar(Pessoa pessoa) {
		pessoaRepository.save(pessoa);
		/* Quando clicar em salvar, vai aparecer a lista de pessoas cadastradas*/
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		andView.addObject("pessoas", pessoasIt);
		andView.addObject("pessoaobj", new Pessoa());

		return andView;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "**/listapessoas")
	public ModelAndView pessoas() {
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		andView.addObject("pessoas", pessoasIt);
		andView.addObject("pessoaobj", new Pessoa());
		return andView;
	}
	
	@GetMapping("**/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll());
		modelAndView.addObject("pessoaobj", pessoa.get());
		return modelAndView;
	}
	
	@GetMapping("/removerpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {
		pessoaRepository.deleteById(idpessoa);
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll());
		modelAndView.addObject("pessoaobj", new Pessoa());
		return modelAndView;
	}
	
	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa, @RequestParam("pesquisasexo") String pesquisasexo ) {
	
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		if(pesquisasexo != null && !pesquisasexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoaBySexo(nomepesquisa, pesquisasexo);
		}else {
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
		}
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoas);
		modelAndView.addObject("pessoaobj", new Pessoa());
		return modelAndView;
	}
	
	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaobj", pessoa.get());
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
		return modelAndView;
	}
	
	@PostMapping("**/addFonePessoa/{pessoaid}")
	public ModelAndView addfonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
		if(telefone != null && telefone.getNumero().isEmpty() 
				|| telefone.getTipo().isEmpty()) {
			ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
			modelAndView.addObject("pessoaobj", pessoa);
			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			List<String> msg = new ArrayList<String>();
			if(telefone.getNumero().isEmpty()) {
			msg.add("Numero deve ser informado");}
			if(telefone.getTipo().isBlank()) {
			msg.add("Tipo deve ser informado");
			}
			modelAndView.addObject("msg", msg);
			return modelAndView;
		}
		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);
		List<String> msg = new ArrayList<String>();
		msg.add("Telefone salvo com sucesso");
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaobj", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
		modelAndView.addObject("msg", msg);
		return modelAndView;
	}
	
	@GetMapping("/removertelefone/{idtelefone}")
	public ModelAndView excluirTelefone(@PathVariable("idtelefone") Long idtelefone) {
		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();
		telefoneRepository.deleteById(idtelefone);
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));
		modelAndView.addObject("pessoaobj", pessoa);
		return modelAndView;
	}
	
}
