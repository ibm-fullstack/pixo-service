package com.pixo.controllers;

import com.cloudinary.utils.ObjectUtils;
import com.pixo.models.*;
import com.pixo.repositories.*;
import com.pixo.request.LoginForm;
import com.pixo.request.SignUpForm;
import com.pixo.request.UploadForm;
import com.pixo.response.JwtResponse;
import com.pixo.response.ResponseMessage;
import com.pixo.response.UploadFileResponse;
import com.pixo.security.*;
import com.pixo.security.jwt.JwtProvider;
import com.pixo.services.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    
    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;
    
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
	@Autowired
	JwtProvider jwtProvider;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    FileItemRepository fileItemRepository;
    
    List<String> files = new ArrayList<String>();

    @RequestMapping("/")
    public String home(Model model){
        List<Image> allImages = (List<Image>) imageRepository.findAll();
        int start = allImages.size()-8;
        if (start<0){start = 0;}
        List<Image> last8 = allImages.subList(start, allImages.size());
        model.addAttribute("imageList", last8);
        return "index";
    }


    @GetMapping("/upload")
    public String uploadForm(Model model){
        model.addAttribute("image", new Image());
        return "upload";
    }
    
//    @PostMapping("/uploadFile")
//    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
//        String fileName = fileStorageService.storeFile(file);
//
//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/downloadFile/")
//                .path(fileName)
//                .toUriString();
//
//        return new UploadFileResponse(fileName, fileDownloadUri,
//                file.getContentType(), file.getSize());
//    }
    
    @PostMapping("/uploadFile")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
    	
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();
        
        UploadFileResponse uploadFileResponse = new UploadFileResponse(fileName, fileDownloadUri,
        		file.getContentType(), file.getSize());

        return new ResponseEntity<>(uploadFileResponse, HttpStatus.OK);
    }

//    @PostMapping("/uploadMultipleFiles")
//    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
//        return Arrays.asList(files)
//                .stream()
//                .map(file -> uploadFile(file))
//                .collect(Collectors.toList());
//    }
    
    @GetMapping("/getallfiles")
    public ResponseEntity<List<String>> getListFiles(HttpServletRequest request) {
	  User user =  getLoggedIn();
	  String authHeader = request.getHeader("Authorization");
      System.out.println("===== authHeader getListFiles: " + authHeader);
	  Iterable<FileItemEntity> fileItemList = fileItemRepository.findAllByUser(user);
	  for(FileItemEntity f: fileItemList) {
		  files.add(f.getFilename());
	  }
      List<String> fileNames = files
          .stream().map(fileName -> MvcUriComponentsBuilder
              .fromMethodName(HomeController.class, "downloadFile", fileName, request).build().toString())
          .collect(Collectors.toList());
   
      return ResponseEntity.ok()
    		  .header(HttpHeaders.AUTHORIZATION, authHeader)
    		  .body(fileNames);
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }
        String authHeader = request.getHeader("Authorization");
        System.out.println("===== authHeader: " + authHeader);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = jwtProvider.generateJwtToken(authentication);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities()));
	
	}

    @GetMapping("/register")
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/signup")
    public ResponseEntity<?> saveAccount(@Valid @RequestBody SignUpForm signUpRequest){
		User user = new User(signUpRequest.getName(), signUpRequest.getUsername(), signUpRequest.getEmail(),
				passwordEncoder.encode(signUpRequest.getPassword()));
		
		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		strRoles.forEach(role -> {
			switch (role) {
			case "admin":
				Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
						.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
				roles.add(adminRole);

				break;
			default:
				Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
						.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
				roles.add(userRole);
			}
		});

		user.setRoles(roles);
		
		userRepository.save(user);

		return new ResponseEntity<>(new ResponseMessage("User registered successfully!"), HttpStatus.OK);
    }
    
    private User getLoggedIn(){
    	
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();		   
		String username = ((UserDetails)principal).getUsername();			
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));

        return user;

    }

}
