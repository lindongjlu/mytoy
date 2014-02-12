
#-------------------------------------------------------------------------------

TARGET = TestCpp

CC     = gcc
CXX    = g++
CFLAGS = -g -fPIC -Wno-deprecated -Wall

INCS   = 
LIBS   = -lpthread
SRCS   = $(sort $(wildcard *.cpp *.cc *.c))
OBJS   = $(patsubst %.cpp,%.o, $(patsubst %.cc,%.o, $(patsubst %.c,%.o, $(SRCS))))
DEPS   = $(foreach obj, $(OBJS), $(dir $(obj)).$(basename $(notdir $(obj))).d)

#-------------------------------------------------------------------------------

all : $(TARGET) $(OBJS)

clean:
     rm -vf $(OBJS) $(DEPS)

#-------------------------------------------------------------------------------

ifneq ($(DEPS),)
-include $(DEPS)
endif

#-------------------------------------------------------------------------------

$(TARGET) : $(OBJS)
     $(CXX) $(CFLAGS) -o $@ $^ $(INCS) $(LIBS)

.%.d: %.cpp
     @echo "update $@ ..."; \
     echo -n $< | sed s/\.cpp/\.o:/ > $@; \
     $(CC) $(INCS) -MM $< | sed '1s/.*.://' >> $@;

%.o: %.cpp
     $(CXX) $(CFLAGS) $(INCS) -o $@ -c $<

.%.d: %.cc
     @echo "update $@ ..."; \
     echo -n $< | sed s/\.c/\.o:/ > $@; \
     $(CC) $(INCS) -MM $< | sed '1s/.*.://' >> $@;

%.o: %.cc
     $(CXX) $(CFLAGS) $(INCS) -o $@ -c $<

.%.d: %.c
     @echo "update $@ ..."; \
     echo -n $< | sed s/\.c/\.o:/ > $@; \
     $(CC) $(INCS) -MM $< | sed '1s/.*.://' >> $@;

%.o: %.c
     $(CC) $(CFLAGS) $(INCS) -o $@ -c $<
    
#-------------------------------------------------------------------------------